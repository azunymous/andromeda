import React from 'react'

function Application({application, headers, mode}) {
    console.log(mode)
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
    console.log(index)
    console.log(headers)
    if (mode === "CONTROLLER" && environment["environmentName"] === headers[index]) {
        return (
            <td className={environment["podController"]["status"]}>
                {environment["podController"]["version"]}
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

function Pods({pods}) {
    return (
        pods.map((pod, index) => {
            return (
                <span key={index} className={pod["status"] + " " + pod["name"]}>
            {pod["version"]}
          </span>
            )
        }))
}


export default Application