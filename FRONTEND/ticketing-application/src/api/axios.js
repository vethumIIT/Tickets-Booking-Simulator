import axios from "axios";


const api = axios.create({
  baseURL: "http://localhost:8080/api/",// Backends URL
  withCredentials: true, // Include cookies with requests
});

export default api;