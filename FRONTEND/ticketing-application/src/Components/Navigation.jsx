//import React from "react";

import { Link } from "react-router-dom";
import './Navigation.css'
import { /*useEffect, useState,*/ useContext } from 'react';


const Navigation = () => {

    return(
        <nav>
            <ul className="navList">
                <li className="listItem">
                <Link to="/" className="listLink">Home</Link>
                </li>

                <li className="listItem">
                <Link to="/menu" className="listLink">Menu</Link>
                </li>


            </ul>
        </nav>
    )

};

export default Navigation;