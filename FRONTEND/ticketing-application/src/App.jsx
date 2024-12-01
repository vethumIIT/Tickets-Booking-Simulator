import { Routes, Route, BrowserRouter } from 'react-router-dom';
import './App.css'

import Navigation from "./Components/Navigation"
import TestPage from './pages/TestPage';
import SimulatorPage from './pages/SimulatorPage';

function App() {

  return (
    <BrowserRouter>
    <Navigation/>
      <Routes>
        <Route index element={<SimulatorPage/>}/>

        <Route path='/test' element={<TestPage/>}/>
      </Routes>
    </BrowserRouter>
  )
}

export default App
