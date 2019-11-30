import React from 'react';
import {useQueryParams, useRoutes} from 'hookrouter';
import './App.css';
import {Nav, Navbar, NavbarBrand, NavItem, NavLink} from "reactstrap";
import DataCentreTabs from "./dashboards/DataCentreTabs";
import TeamDashboard from "./dashboards/TeamDashboard";

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
    const [queryParams, setQueryParams] = useQueryParams();
    const {
        mode = 'CONTROLLER'
    } = queryParams;


    const viewHandler = (view) => {
        setQueryParams({mode: view});
    };

    function activeIf(link) {
        if (mode === link) {
            return "active"
        }
        return "disabled"
    }

    return (
        <div className="container-fluid min-vh-100 bg-dark">
            <Navbar color="dark" dark expand="md">
                <NavbarBrand href="/">{team}</NavbarBrand>
                <Nav className="mr-auto" tabs>
                    <NavItem>
                        <NavLink onClick={() => viewHandler("CONTROLLER")}
                                 active={mode === "CONTROLLER"}>Controllers</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink onClick={() => viewHandler("POD")} active={mode === "POD"}>Pods</NavLink>
                    </NavItem>
                    <NavItem>
                        <NavLink disabled href="#">Dependencies</NavLink>
                    </NavItem>
                </Nav>
                <DataCentreTabs team={team}/>
            </Navbar>
            <TeamDashboard team={team}/>
        </div>
    );
}

function NotFoundPage() {
    return (
        <div>404 NOT FOUND</div>
    )
}


export default App;
