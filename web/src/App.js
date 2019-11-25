import React from 'react';
import {useRoutes, useRedirect} from 'hookrouter';
import './App.css';
import OuterTable from "./dashboards/OuterTable";

function App() {
    const routes = {
        '/': () => <HomePage/>,
        '/:team*': ({team}) => <Dashboard team={team}/>,
    };

    const routeResult = useRoutes(routes);
    return routeResult || <NotFoundPage/>;
}

function HomePage() {
    return (
        <ul>
            <li><a href={"/andromeda/"}>andromeda</a></li>
            <li><a href={"/acme/"}>acme</a></li>
        </ul>
    )
}


function Dashboard({team}) {
    return (
        <div>
            <h3>Team: {team}</h3>
            <OuterTable team={team}/>
        </div>
    );
}

function NotFoundPage() {
    return (
        <div>404 NOT FOUND</div>
    )
}


export default App;
