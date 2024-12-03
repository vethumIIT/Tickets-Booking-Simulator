# Ticket Booking Simulator

## Introduction

This Ticket Booking Simulator is a multi-threaded Application that simulates a real-time ticket booking application with multiple vendor threads adding tickets and customer threads buying tickets.

## Setup Instructions

### 1. Prerequisits

- `Java 17` or later.

- `npm - version 10.9.0` or later.

- A Modern Browser (Eg: `Google Chrome`, `Microsoft Edge` etc.)

- `Node JS - v22.11.0` or later

- `Intellij IDEA Community Edition`

### 2. How to run the application

clone the Repo with github repository using the following commands if you have git on your pc.

    git clone https://github.com/vethumIIT/Tickets-Booking-Simulator.git
    cd ticket-booking-simulator

Otherwise you can directly download the Zip Folder and extract it to your preferred file location.

To use this application you have 2 options

1. Run the **Web Application**. For this you need to run the `Spring Boot` application and the `React` Application. You can use this method for a better user experience. with real time statistics and beautiful user interface.
2. Run the **CLI (Command Line Interface)** application to take your inputs and run the application that way.

### 1. Web Application

#### 1. Get the Backend Running (`Spring Boot`)

1. Step 1 Go to the following folder.`<Your Project file Location>\Tickets-Booking-Simulator\BACKEND`

2. Step 2: Open the relavent program in `Intellij IDEA`.

3. Step 3: In `Intellij IDEA` and locate the file `<Your project file location>\Tickets-Booking-Simulator\BACKEND\ticket-booking-simulator\src\main\java\com\app\ticket_booking_simulator\TicketBookingSimApp.java` and run that file.

4. Now you have the backend running.

#### 2. Get the Frontend Running (`ReactJS`)

1. Step 1 Go to the following folder.`<Your Project file Location>\Tickets-Booking-Simulator\FRONTEND\ticketing-application`

2. Step 2: Open `Command Prompt` in the relavent folder

3. Step 3: type the command `npm run dev`, and the react app will start running.

4. Now you have the frontend running. To access it  open your browser and enter `http://localhost:5173/` in your address bar.

### 2. CLI (Option).

1. Step 1 Go to the following folder.`<Your Project file Location>\Tickets-Booking-Simulator\BACKEND`

2. Step 2: Open the relavent program in `Intellij IDEA`.

3. Step 3: In `Intellij IDEA` and locate the file `<Your project file location>\Tickets-Booking-Simulator\BACKEND\ticket-booking-simulator\src\main\java\com\app\ticket_booking_simulator\TicketingApplicationCLI.java` and run that file.


## How to use the application

After Setting Up the Application you can start using the application.

When you start either the CLI application or the Web Application. You will be prompted 4 values. Below are what those values mean.

1. **`Total Tickets`** - This is the total number of tickets that will be added to the ticket pool by the vendors.
2. **`Ticket Release Rate`** - This value represents the number of tickets released by a single vendor per second.
3. **`Customer Retrieval Rate`** - This value represents the number of tickets each customer tries to buy per second.
4. **`Maximum Ticket Capacity`** - This is the maximum number of tickets that can be in the ticket pool at a given time. when this number is reached, the ticket pool will not allow vendors to add their tickets until a customer purchases a ticket and reduces the ticket count.

### 1. CLI Option

When you run the CLI, you will be prompted to enter the above mentioned values as shown bellow.

    Enter total tickets: 100
    Enter ticket release rate: 20
    Enter customer retrieval rate: 10
    Enter maximum ticket capacity: 30

Once you enter the final value, the simulation will start running and the logs of the program will be displayed.

    Enter total tickets: 100
    Enter ticket release rate: 20
    Enter customer retrieval rate: 10
    Enter maximum ticket capacity: 30
    Database Opened successfully!
    Table Created Successfully!
    Vendors Started
    Vendor 7 added ticket no 1
    Customer 6 booked ticket no 1
    Vendor 6 added ticket no 2
    Customer 5 booked ticket no 2
    Vendor 5 added ticket no 3
    Customer 4 booked ticket no 3
    Vendor 4 added ticket no 4
    Customer 3 booked ticket no 4
    ...

Once the Simulation ends the following information will be displayed on the screen.

    ...
    Total Ticket Count Reached! no more tickets can be added.
    Tickets in ticket pool: 0
    Added Tickets Count: 100
    Booked tickets count: 100


### 2. Web Application.

Once you have the `React` Application and the `Spring Boot` application running, open your browser and go to `http://localhost:5173/`.


