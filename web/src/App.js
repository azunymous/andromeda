import React from 'react';
import {useRoutes} from 'hookrouter';
import './App.css';
import OuterTable from "./dashboards/OuterTable";

function App() {
    const routes = {
        '/': () => <HomePage/>,
        '/:team': ({team}) => <Dashboard team={team}/>,
        '/:team/:dataCentre': ({team, dataCentre}) => <Dashboard team={team} dataCentre={dataCentre}/>,
    };

    const routeResult = useRoutes(routes);
    return routeResult || <NotFoundPage/>;
}

function HomePage() {
    return (
        <ul>
            <li><a href={"/andromeda"}>andromeda</a></li>
            <li><a href={"/acme"}>acme</a></li>
        </ul>
    )
}


function Dashboard({team, dataCentre}) {
    return (
        <div>
            <h3>Team: {team}</h3>
            <OuterTable team={team} dataCentre={dataCentre}/>
        </div>
    );
}

function NotFoundPage() {
    return (
        <div>404 NOT FOUND</div>
    )
}


export default App;
