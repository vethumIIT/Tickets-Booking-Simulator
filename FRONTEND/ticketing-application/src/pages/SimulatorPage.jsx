import { useState, useEffect, useRef } from "react"
import "./form.css"


const SimulatorPage = () => {

    const [inputs, setInputs] = useState({});
    const [logs, setLogs] = useState([""]);
    const [runningSimulation, setRunningSimulation] = useState(false);
    const divRef = useRef(null);

    var [firstRun, setFirstRun]=useState(true);

    var [updateTime, setUpdateTime]=useState(1000);

    var [customerBookings, setCustomerBookings] = useState();
    var [vendorTickets, setVendorTickets] = useState();
    var [ticketPool, setTicketPool] = useState([]);

    var [stats, setStats] = useState({
        "ticketBookedCount":0,
        "ticketCount":0,
        "ticketListSize":0
    });


    useEffect(()=>{

        if(runningSimulation==true){
            setUpdateTime(200);
        }else{
            setUpdateTime(1000);
        }
        
        const interval = setInterval(() => { // Update state every second
            
            if(runningSimulation==true || firstRun==true){
                setFirstRun(false);
                console.log("Poll Logs");
                getLogs();
                getCustomerBookings();
                getVendorTickets();
                getTicketPool();
                getStats();
                //checkRunning();

                if (divRef.current) {
                    divRef.current.scrollTop = divRef.current.scrollHeight;
                }
            }else{
                console.log("update");
            }
            
          }, updateTime);
        
          return () => clearInterval(interval);

    }, [runningSimulation, logs, firstRun, updateTime])

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
            if (result) {
                //console.log("Result: ",result);
                setLogs(result);
            }
            
        } catch (error) {
            console.error("Error in getLogs request:", error);
            
        }
    }

    const getStats = async () => {
        try {
            const response = await fetch("http://localhost:8080/api/get_stats", {
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
            if (result) {
                console.log("Stats: ",result);
                setStats(result);
            }
            
        } catch (error) {
            console.error("Error in getStats request:", error);
            
        }
    }

    const getCustomerBookings = async () => {
        try {
            const response = await fetch("http://localhost:8080/api/get_customer_bookings", {
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
            if (result){
                //console.log("Result: ",result);
                setCustomerBookings(result);
            }
            //console.log("customerBookings: ",result);
            
        } catch (error) {
            console.error("Error in getCustomerBookings request:", error);
        }
    }

    const getVendorTickets = async () => {
        //setRunningSimulation(true);
        try {
            const response = await fetch("http://localhost:8080/api/get_vendor_tickets", {
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
            if (result){
                //console.log("Result: ",result);
                setVendorTickets(result);
            }
            //console.log("vendorTickets: ",result);

        } catch (error) {
            console.error("Error in getVendorTickets request:", error);
        }
    }

    const getTicketPool = async () => {
        //setRunningSimulation(true);
        try {
            const response = await fetch("http://localhost:8080/api/get_tickets", {
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
            if (result){
                //console.log("Result: ",result);
                setTicketPool(result);
            }

        } catch (error) {
            console.error("Error in getTicketPool request:", error);
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
            console.error("Error in hadnleStart request:", error);
        }

    }

    const handleStop = async () => {
        try {
            const response = await fetch("http://localhost:8080/api/stop", {
                method: "POST", // HTTP method
                headers: {
                    "Content-Type": "application/json", // Specify JSON format
                },
                body: JSON.stringify({}), // Convert JavaScript object to JSON string
            });

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            const result = await response.text(); // Parse the response
            alert(result); // Handle the response
            //setRunningSimulation(false);
        } catch (error) {
            console.error("Error in handleStop request:", error);
        }

    }

    const checkRunning = async () => {
        try {
            const response = await fetch("http://localhost:8080/api/is_running", {
                method: "POST", // HTTP method
                headers: {
                    "Content-Type": "application/json", // Specify JSON format
                },
                body: JSON.stringify({}), // Convert JavaScript object to JSON string
            });

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            const result = await response.text(); // Parse the response
            if (result){
                console.log("Result: ",result);
                if(result=="true"){
                    setRunningSimulation(true);
                }else{
                    setRunningSimulation(false);
                }
            }
            console.log("Running: ",result); 
        } catch (error) {
            console.error("Error in checkRunning request:", error);
        }

    }

    return(
        <>
        <div id="mainDiv">
            <div id="formDiv">
                <h2>Enter Values</h2>
                <form onSubmit={handleSubmit}>
                    <label htmlFor="totalTickets">Total Tickets:<br/>
                        <input type="number" name="totalTickets" id="totalTickets" onChange={handleChange}/>
                    </label>
                    <label htmlFor="ticketReleaseRate">Ticket release rate:<br/>
                        <input type="number" name="ticketReleaseRate" id="ticketReleaseRate" onChange={handleChange}/>
                    </label>
                    <label htmlFor="customerRetrievalRate">Customer retrieval rate: <br/>
                        <input type="number" name="customerRetrievalRate" id="customerRetrievalRate" onChange={handleChange}/>
                    </label>
                    <label htmlFor="maxTicketCapacity">Maximum ticket capacity: <br/>
                        <input type="number" name="maxTicketCapacity" id="maxTicketCapacity" onChange={handleChange}/>
                    </label>
                    <br/>
                    <input type='Submit' name='submit' id='submit' value="Start"/>
                    
                </form>
                <button id="stop" onClick={handleStop}>Stop</button>

            </div>

            <div id="logsSection">
            <h3>Logs</h3>
                <div className="logsDiv" ref={divRef}>
                    <ul>
                        {logs.length? (
                            <>
                            {logs.map((log, index)=>(
                                <li key={index}>{log}</li>
                            ))}
                            </>
                        ):(<></>)}

                    </ul>
                </div>
            </div>

        </div>


        <div id="visualSection">
            <h3>Stats Display</h3>
            <div id="stats">
                <table>
                    <tr><th>Number of Tickets Added By Vendors :   </th>
                        <td className="tableStats">{stats.ticketBookedCount}</td>
                    </tr>
                    <tr>
                        <th>Number of Tickets Purchased By Customers :   </th>
                        <td className="tableStats">{stats.ticketCount}</td>
                        </tr>
                    <tr>
                        <th>Number of Tickets in Ticket Pool :   </th>
                        <td className="tableStats">{stats.ticketListSize}</td>
                    </tr>
                </table>
            </div>
            <div id="visualisationDiv">
                <div id="customerVisual" className="visualElement">
                    <h3>Customers</h3>
                    <table>
                        <tr>
                            <th>Customer Name</th>
                            <th>Tickets Bought</th>
                        </tr>
                        {customerBookings!=null?(
                            <>
                            {Object.keys(customerBookings).map(
                            (key) => (
                                <tr key={key}>
                                <td>Customer {key}</td><td>{customerBookings[key]} </td>
                                </tr>
                            ))}
                            </>
                        ):(<></>)}
                        
                    </table>

                </div>

                <div id="ticketPoolVisual" className="visualElement">
                    <h3>Ticket Pool</h3>
                    <ol>
                    {ticketPool!=null? (
                        <>
                        {ticketPool.map((ticket, index)=>(
                            <>                                    
                            <li key={index}>Ticket No {ticket["ticketId"]}</li>                                    
                            </>
                        ))}
                        </>
                    ):(<></>)}
                    </ol>

                </div>

                <div id="vendorVisual" className="visualElement">
                    <h3>Vendors</h3>
                    <table>
                        <tr>
                            <th>Vendor Name</th>
                            <th>Tickets Added</th>
                        </tr>
                    {vendorTickets!=null?(
                            <>
                            {Object.keys(vendorTickets).map(
                            (key) => (
                                <tr key={key}>
                                <td>Vendor {key}</td><td>{vendorTickets[key]} </td>
                                </tr>
                            ))}
                            </>
                        ):(<></>)}

                    </table>

                </div>
            </div>

        </div>
        </>
    )
}

export default SimulatorPage;