import { useState, useEffect, useRef } from "react"
import "./form.css"


const SimulatorPage = () => {

    const [updateCount, setUpdateCount] = useState(0);
    const [inputs, setInputs] = useState({});
    const [logs, setLogs] = useState([""]);
    const [runningSimulation, setRunningSimulation] = useState(false);
    const divRef = useRef(null);

    var [firstRun, setFirstRun]=useState(true);

    var [updateTime, setUpdateTime]=useState(1000);


    useEffect(()=>{

        if(runningSimulation){
            setUpdateTime(20);
        }else{
            setUpdateTime(1000);
        }
        
        const interval = setInterval(() => {
            setUpdateCount((updateCount) => updateCount + 1); // Update state every second
            console.log("update");
            if(runningSimulation==true || firstRun==true){
                setFirstRun(false);
                console.log("Poll Logs");
                getLogs();

                if (divRef.current) {
                    divRef.current.scrollTop = divRef.current.scrollHeight;
                }
            }
            
          }, 20);
        
          return () => clearInterval(interval);

    }, [runningSimulation, logs])

    const getLogs = async () => {
        //setRunningSimulation(true);
        try {
            const response = await fetch("http://localhost:8080/api/get_logs", {
                method: "POST", // HTTP method
                headers: {
                    "Content-Type": "application/json", // Specify JSON format
                },
                body: JSON.stringify({}), 
            });

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            const result = await response.json(); // Parse the response
            setLogs(result);
            //console.log(result);
            //alert(result.message); // Handle the response
        } catch (error) {
            console.error("Error in POST request:", error);
        }
    }


    const handleChange = (event) => {
        const name = event.target.name;
        const value = event.target.value;

        setInputs(values => ({...values, [name]: value}))
    }

    const handleSubmit = (event) => {
        event.preventDefault();


        if(inputs["totalTickets"]==null || inputs["totalTickets"]==''){
            console.log("total tickets must have a value");
            return;
        }else if(inputs["ticketReleaseRate"]==null || inputs["ticketReleaseRate"]==''){
            console.log("ticket release rate must have a value");
            return;
        }else if(inputs["customerRetrievalRate"]==null || inputs["customerRetrievalRate"]==''){
            console.log("customer retrieval rate must have a value");
            return;
        }else if(inputs["maxTicketCapacity"]==null || inputs["maxTicketCapacity"]==''){
            console.log("maximum ticket capacity must have a value");
            return;
        }

        if(inputs["totalTickets"]<1){
            console.log("totalTickets must be 1 or higher");
            return;
        }if(inputs["ticketReleaseRate"]<1){
            console.log("Ticket Release Rate must be 1 or higher");
            return;
        }if(inputs["customerRetrievalRate"]<1){
            console.log("Customer Retrieval Rate must be 1 or higher");
            return;
        }if(inputs["maxTicketCapacity"]<1){
            console.log("max ticket capacity must be 1 or higher");
            return;
        }if(Number(inputs["maxTicketCapacity"])<Number(inputs["totalTickets"])){
            console.log("max ticket capacity cannot be less than total tickets");
            return;
        }

        alert("All values are valid!");

        const data = {
            "totalTickets":inputs["totalTickets"],
            "ticketReleaseRate":inputs["ticketReleaseRate"],
            "customerRetrievalRate":inputs["customerRetrievalRate"],
            "maxTicketCapacity":inputs["maxTicketCapacity"]
        };

        handleStart(data);
        
    }

    const handleStart = async (data) => {
        setRunningSimulation(true);
        setLogs([""]);
        try {
            const response = await fetch("http://localhost:8080/api/start", {
                method: "POST", // HTTP method
                headers: {
                    "Content-Type": "application/json", // Specify JSON format
                },
                body: JSON.stringify(data), // Convert JavaScript object to JSON string
            });

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            const result = await response.text(); // Parse the response
            alert(result); // Handle the response
            setRunningSimulation(false);
        } catch (error) {
            console.error("Error in POST request:", error);
        }

    }

    return(
        <>
        <h1>This is the Simulation Page for the ticket booking Simulator</h1>
        <div id="mainDiv">
            <div id="formDiv">
                <h2>Enter Values</h2>
                <form onSubmit={handleSubmit}>
                    <label htmlFor="totalTickets">Enter ticket count:<br/>
                        <input type="number" name="totalTickets" id="totalTickets" onChange={handleChange}/>
                    </label>
                    <label htmlFor="ticketReleaseRate">Enter ticket release rate:<br/>
                        <input type="number" name="ticketReleaseRate" id="ticketReleaseRate" onChange={handleChange}/>
                    </label>
                    <label htmlFor="customerRetrievalRate">Enter customer retrieval rate: <br/>
                        <input type="number" name="customerRetrievalRate" id="customerRetrievalRate" onChange={handleChange}/>
                    </label>
                    <label htmlFor="maxTicketCapacity">Enter maximum ticket capacity: <br/>
                        <input type="number" name="maxTicketCapacity" id="maxTicketCapacity" onChange={handleChange}/>
                    </label>
                    <br/>
                    <input type='Submit' name='submit' id='submit' value="Start"/>
                </form>

            </div>

            <div id="logsSection">
            <h3>Logs</h3>
                <div className="logsDiv" ref={divRef}>
                    
                    <ul>
                        {logs.length? (
                            <>
                            {logs.map((log, index)=>(
                                <>
                                <li key={index}>{log}</li>
                                </>
                            ))}
                            </>
                    ):(<></>)}

                    </ul>
                </div>
            </div>

        </div>
        </>
    )
}

export default SimulatorPage;