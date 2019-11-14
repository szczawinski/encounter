'use strict';

const React = require('react');
const ReactDOM = require('react-dom');
const when = require('when');
const client = require('./client');

const follow = require('./follow'); // function to hop multiple links by "rel"

const stompClient = require('./websocket-listener');

const root = '/api';

class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = {games: [], attributes: [], page: 1, pageSize: 2, links: {}
		   , loggedInManager: this.props.loggedInManager};
		this.updatePageSize = this.updatePageSize.bind(this);
		this.onCreate = this.onCreate.bind(this);
		this.onUpdate = this.onUpdate.bind(this);
		this.onDelete = this.onDelete.bind(this);
		this.onNavigate = this.onNavigate.bind(this);
		this.refreshCurrentPage = this.refreshCurrentPage.bind(this);
		this.refreshAndGoToLastPage = this.refreshAndGoToLastPage.bind(this);
	}

	loadFromServer(pageSize) {
		follow(client, root, [
				{rel: 'games', params: {size: pageSize}}]
		).then(gameCollection => {
			return client({
				method: 'GET',
				path: gameCollection.entity._links.profile.href,
				headers: {'Accept': 'application/schema+json'}
			}).then(schema => {
				// tag::json-schema-filter[]
				/**
				 * Filter unneeded JSON Schema properties, like uri references and
				 * subtypes ($ref).
				 */
				Object.keys(schema.entity.properties).forEach(function (property) {
					if (schema.entity.properties[property].hasOwnProperty('format') &&
						schema.entity.properties[property].format === 'uri') {
						delete schema.entity.properties[property];
					}
					else if (schema.entity.properties[property].hasOwnProperty('$ref')) {
						delete schema.entity.properties[property];
					}
				});

				this.schema = schema.entity;
				this.links = gameCollection.entity._links;
				return gameCollection;
				// end::json-schema-filter[]
			});
		}).then(gameCollection => {
			this.page = gameCollection.entity.page;
			return gameCollection.entity._embedded.games.map(game =>
					client({
						method: 'GET',
						path: game._links.self.href
					})
			);
		}).then(gamePromises => {
			return when.all(gamePromises);
		}).done(games => {
			this.setState({
				page: this.page,
				games: games,
				attributes: Object.keys(this.schema.properties),
				pageSize: pageSize,
				links: this.links
			});
		});
	}

	// tag::on-create[]
	onCreate(newGame) {
		follow(client, root, ['games']).done(response => {
			client({
				method: 'POST',
				path: response.entity._links.self.href,
				entity: newGame,
				headers: {'Content-Type': 'application/json'}
			})
		})
	}
	// end::on-create[]

	// tag::on-update[]
	onUpdate(game, updatedGame) {
		if(game.entity.manager.name === this.state.loggedInManager) {
			updatedGame["manager"] = game.entity.manager;
			client({
				method: 'PUT',
				path: game.entity._links.self.href,
				entity: updatedGame,
				headers: {
					'Content-Type': 'application/json',
					'If-Match': game.headers.Etag
				}
			}).done(response => {
				/* Let the websocket handler update the state */
			}, response => {
				if (response.status.code === 403) {
					alert('ACCESS DENIED: You are not authorized to update ' +
						game.entity._links.self.href);
				}
				if (response.status.code === 412) {
					alert('DENIED: Unable to update ' + game.entity._links.self.href +
						'. Your copy is stale.');
				}
			});
		} else {
			alert("You are not authorized to update");
		}
	}
	// end::on-update[]

	// tag::on-delete[]
	onDelete(game) {
		client({method: 'DELETE', path: game.entity._links.self.href}
		).done(response => {/* let the websocket handle updating the UI */},
		response => {
			if (response.status.code === 403) {
				alert('ACCESS DENIED: You are not authorized to delete ' +
					game.entity._links.self.href);
			}
		});
	}
	// end::on-delete[]

	onNavigate(navUri) {
		client({
			method: 'GET',
			path: navUri
		}).then(gameCollection => {
			this.links = gameCollection.entity._links;
			this.page = gameCollection.entity.page;

			return gameCollection.entity._embedded.games.map(game =>
					client({
						method: 'GET',
						path: game._links.self.href
					})
			);
		}).then(gamePromises => {
			return when.all(gamePromises);
		}).done(games => {
			this.setState({
				page: this.page,
				games: games,
				attributes: Object.keys(this.schema.properties),
				pageSize: this.state.pageSize,
				links: this.links
			});
		});
	}

	updatePageSize(pageSize) {
		if (pageSize !== this.state.pageSize) {
			this.loadFromServer(pageSize);
		}
	}

	// tag::websocket-handlers[]
	refreshAndGoToLastPage(message) {
		follow(client, root, [{
			rel: 'games',
			params: {size: this.state.pageSize}
		}]).done(response => {
			if (response.entity._links.last !== undefined) {
				this.onNavigate(response.entity._links.last.href);
			} else {
				this.onNavigate(response.entity._links.self.href);
			}
		})
	}

	refreshCurrentPage(message) {
		follow(client, root, [{
			rel: 'games',
			params: {
				size: this.state.pageSize,
				page: this.state.page.number
			}
		}]).then(gameCollection => {
			this.links = gameCollection.entity._links;
			this.page = gameCollection.entity.page;

			return gameCollection.entity._embedded.games.map(game => {
				return client({
					method: 'GET',
					path: game._links.self.href
				})
			});
		}).then(gamePromises => {
			return when.all(gamePromises);
		}).then(games => {
			this.setState({
				page: this.page,
				games: games,
				attributes: Object.keys(this.schema.properties),
				pageSize: this.state.pageSize,
				links: this.links
			});
		});
	}
	// end::websocket-handlers[]

	// tag::register-handlers[]
	componentDidMount() {
		this.loadFromServer(this.state.pageSize);
		stompClient.register([
			{route: '/topic/newGame', callback: this.refreshAndGoToLastPage},
			{route: '/topic/updateGame', callback: this.refreshCurrentPage},
			{route: '/topic/deleteGame', callback: this.refreshCurrentPage}
		]);
	}
	// end::register-handlers[]

	render() {
		return (
			<div>
				<CreateDialog attributes={this.state.attributes} onCreate={this.onCreate}/>
				<GameList page={this.state.page}
							  games={this.state.games}
							  links={this.state.links}
							  pageSize={this.state.pageSize}
							  attributes={this.state.attributes}
							  onNavigate={this.onNavigate}
							  onUpdate={this.onUpdate}
							  onDelete={this.onDelete}
							  updatePageSize={this.updatePageSize}
							  loggedInManager={this.state.loggedInManager}/>
			</div>
		)
	}
}

class CreateDialog extends React.Component {

	constructor(props) {
		super(props);
		this.handleSubmit = this.handleSubmit.bind(this);
	}

	handleSubmit(e) {
		e.preventDefault();
		const newGame = {};
		this.props.attributes.forEach(attribute => {
			newGame[attribute] = ReactDOM.findDOMNode(this.refs[attribute]).value.trim();
		});
		this.props.onCreate(newGame);
		this.props.attributes.forEach(attribute => {
			ReactDOM.findDOMNode(this.refs[attribute]).value = ''; // clear out the dialog's inputs
		});
		window.location = "#";
	}

	render() {
		const inputs = this.props.attributes.map(attribute =>
			<p key={attribute}>
				<input type="text" placeholder={attribute} ref={attribute} className="field"/>
			</p>
		);
		return (
			<div>
				<a href="#createGame">Create</a>

				<div id="createGame" className="modalDialog">
					<div>
						<a href="#" title="Close" className="close">X</a>

						<h2>Create new game</h2>

						<form>
							{inputs}
							<button onClick={this.handleSubmit}>Create</button>
						</form>
					</div>
				</div>
			</div>
		)
	}
}

class UpdateDialog extends React.Component {

	constructor(props) {
		super(props);
		this.handleSubmit = this.handleSubmit.bind(this);
	}

	handleSubmit(e) {
		e.preventDefault();
		const updatedGame = {};
		this.props.attributes.forEach(attribute => {
			updatedGame[attribute] = ReactDOM.findDOMNode(this.refs[attribute]).value.trim();
		});
		this.props.onUpdate(this.props.game, updatedGame);
		window.location = "#";
	}

	render() {
		const inputs = this.props.attributes.map(attribute =>
			<p key={this.props.game.entity[attribute]}>
				<input type="text" placeholder={attribute}
					   defaultValue={this.props.game.entity[attribute]}
					   ref={attribute} className="field"/>
			</p>
		);

		const dialogId = "updateGame-" + this.props.game.entity._links.self.href;

		const isManagerCorrect = this.props.game.entity.manager.name == this.props.loggedInManager;

		if (isManagerCorrect === false) {
			return (
					<div>
						<a>Not Your Game</a>
					</div>
				)
		} else {
			return (
				<div>
					<a href={"#" + dialogId}>Update</a>
	
					<div id={dialogId} className="modalDialog">
						<div>
							<a href="#" title="Close" className="close">X</a>
	
							<h2>Update an game</h2>
	
							<form>
								{inputs}
								<button onClick={this.handleSubmit}>Update</button>
							</form>
						</div>
					</div>
				</div>
			)
		}
	}

}

class GameList extends React.Component {

	constructor(props) {
		super(props);
		this.handleNavFirst = this.handleNavFirst.bind(this);
		this.handleNavPrev = this.handleNavPrev.bind(this);
		this.handleNavNext = this.handleNavNext.bind(this);
		this.handleNavLast = this.handleNavLast.bind(this);
		this.handleInput = this.handleInput.bind(this);
	}

	handleInput(e) {
		e.preventDefault();
		const pageSize = ReactDOM.findDOMNode(this.refs.pageSize).value;
		if (/^[0-9]+$/.test(pageSize)) {
			this.props.updatePageSize(pageSize);
		} else {
			ReactDOM.findDOMNode(this.refs.pageSize).value = pageSize.substring(0, pageSize.length - 1);
		}
	}

	handleNavFirst(e) {
		e.preventDefault();
		this.props.onNavigate(this.props.links.first.href);
	}

	handleNavPrev(e) {
		e.preventDefault();
		this.props.onNavigate(this.props.links.prev.href);
	}

	handleNavNext(e) {
		e.preventDefault();
		this.props.onNavigate(this.props.links.next.href);
	}

	handleNavLast(e) {
		e.preventDefault();
		this.props.onNavigate(this.props.links.last.href);
	}

	render() {
		const pageInfo = this.props.page.hasOwnProperty("number") ?
			<h3>Games - Page {this.props.page.number + 1} of {this.props.page.totalPages}</h3> : null;

		const games = this.props.games.map(game =>
			<Game key={game.entity._links.self.href}
					  game={game}
					  attributes={this.props.attributes}
					  onUpdate={this.props.onUpdate}
					  onDelete={this.props.onDelete}
					  loggedInManager={this.props.loggedInManager}/>
		);

		const navLinks = [];
		if ("first" in this.props.links) {
			navLinks.push(<button key="first" onClick={this.handleNavFirst}>&lt;&lt;</button>);
		}
		if ("prev" in this.props.links) {
			navLinks.push(<button key="prev" onClick={this.handleNavPrev}>&lt;</button>);
		}
		if ("next" in this.props.links) {
			navLinks.push(<button key="next" onClick={this.handleNavNext}>&gt;</button>);
		}
		if ("last" in this.props.links) {
			navLinks.push(<button key="last" onClick={this.handleNavLast}>&gt;&gt;</button>);
		}

		return (
			<div>
				{pageInfo}
				<input ref="pageSize" defaultValue={this.props.pageSize} onInput={this.handleInput}/>
				<table>
					<tbody>
						<tr>
							<th>Game Name</th>
							<th>Manager</th>
							<th></th>
							<th></th>
						</tr>
						{games}
					</tbody>
				</table>
				<div>
					{navLinks}
				</div>
			</div>
		)
	}
}

// tag::game[]
class Game extends React.Component {

	constructor(props) {
		super(props);
		this.handleDelete = this.handleDelete.bind(this);
	}

	handleDelete() {
		this.props.onDelete(this.props.game);
	}

	render() {
		return (
			<tr>
				<td>{this.props.game.entity.name}</td>
				<td>{this.props.game.entity.lastName}</td>
				<td>{this.props.game.entity.manager.name}</td>
				<td>
					<UpdateDialog game={this.props.game}
								  attributes={this.props.attributes}
								  onUpdate={this.props.onUpdate}
								  loggedInManager={this.props.loggedInManager}/>
				</td>
				<td>
					<button onClick={this.handleDelete}>Delete</button>
				</td>
			</tr>
		)
	}
}
// end::game[]

ReactDOM.render(
	<App loggedInManager={document.getElementById('managername').innerHTML } />,
	document.getElementById('react')
)
