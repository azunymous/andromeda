import React from 'react'
import {Badge, Button} from "reactstrap";

function Application({application, headers, mode}) {
    return (
        <tr>
            <th scope="row">{application["name"]}</th>
            {application["environments"].map((environment, index) => {
                return <Environment key={index} index={index} environment={environment} headers={headers}
                                    mode={mode}/>
            })}
        </tr>
    )
}

function Environment({index, environment, headers, mode}) {
    if (mode === "CONTROLLER" && environment["environmentName"] === headers[index]) {
        return (
            <td className={environment["podController"]["status"]}>
                <Button size="lg" block
                        color={colorFrom(environment["podController"]["status"])}> {environment["podController"]["version"]} </Button>
            </td>
        )
    } else if ((mode === "POD" || mode === "DEPENDENCY") && environment["environmentName"] === headers[index]) {
        return (
            <td>
                <Pods key={index} pods={environment["podController"]["pods"]} dependencies={mode === "DEPENDENCY"}/>
            </td>
        )
    }

    return (
        <td className={"EMPTY"}/>
    )
}

function colorFrom(status) {
    if (status === "READY") {
        return "success"
    } else if (status === "LIVE" || status === "SCALED_DOWN") {
        return "warning"
    } else if (status === "UNAVAILABLE") {
        return "danger"
    } else {
        return ""
    }
}

function colorFromGauge(up) {
    if (up) {
        return "success"
    } else {
        return "danger"
    }
}

function Pods({pods, dependencies}) {
    return (
        pods.map((pod, index) => {
            return (
                <Button key={index} color={colorFrom(pod["status"])} className={pod["status"] + " " + pod["name"]}>
                    {pod["version"]}
                    <Dependencies enabled={dependencies} dependencies={pod["dependencies"]}/>
                </Button>
            )
        }))
}

function Dependencies({enabled, dependencies}) {
    console.log(dependencies);
    if (enabled) {
        return (
            <div>
                {dependencies.map((dependency, index) => {
                    return <Badge key={index} color={colorFromGauge(dependency["up"])} pill>{dependency["name"]}</Badge>
                })}
            </div>
        )
    }
    return (<span/>)
}

export default Application