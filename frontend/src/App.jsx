import { useState } from 'react'
import './App.css'
import Dashboard from './pages/Dashboard';

function App() {
  return (
    <div className="min-h-screen flex">
      <div className="flex-[6]"><Dashboard /></div>
      <div className="flex-[2] bg-gray-100 mt-4 mb-4"></div>
    </div>
  )
}

export default App
