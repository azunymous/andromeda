import React from 'react'
import {Button} from "reactstrap";

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
    console.log(index);
    console.log(headers);
    if (mode === "CONTROLLER" && environment["environmentName"] === headers[index]) {
        return (
            <td className={environment["podController"]["status"]}>
                <Button  size="lg" block
                    color={colorFrom(environment["podController"]["status"])}> {environment["podController"]["version"]} </Button>
            </td>
        )
    } else if (mode === "POD" && environment["environmentName"] === headers[index]) {
        return (
            <td>
                <Pods key={index} pods={environment["podController"]["pods"]}/>
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

function Pods({pods}) {


    return (
        pods.map((pod, index) => {
            return (
                <Button key={index} color={colorFrom(pod["status"])} className={pod["status"] + " " + pod["name"]}>
                    {pod["version"]}
                </Button>
            )
        }))
}


export default Application