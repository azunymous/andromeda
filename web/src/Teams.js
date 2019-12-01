import React, {useEffect, useState} from 'react'
import {Container} from "reactstrap";
import Config from "./options/Configuration";


function Teams() {
    let [isLoading, setIsLoading] = useState(true);
    let [teams, setTeams] = useState([]);


    useEffect(fetchTeams, []);

    function fetchTeams() {
        if (teams.length === 0) {
            console.log("Getting teams");
            fetch(Config.getAPIURL() + '/config/')
                .then(
                    response => {
                        if (response.status === 200) {
                            return response.json()
                        } else {
                            //bad data
                            console.error("Could not find teams");
                            return false;
                        }
                    }
                )
                .then(
                    data => {
                        if (data === false) {
                            // Handle error and retry
                        } else {
                            setTeams(data["teams"]);
                            setIsLoading(false);
                            console.log(teams)
                        }
                    }
                )
                .catch(e => console.log(e));
        }
    }

    if (isLoading) {
        return (
            <div>
                Loading...
            </div>
        )
    } else {
        return (

            <div>
                <Container>
                    <h1 className="text-center display-1 text-white-50">Andromeda Dashboard</h1>
                </Container>
                {
                    teams.map((team, index) => {
                        return (
                            <p key={index} className={"text-center display-4 text-white"}><a
                                href={"/" + team + "/"}>{team}</a>
                            </p>

                        )
                    })
                }
            </div>
        )
    }
}

export default Teams