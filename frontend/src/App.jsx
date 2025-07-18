import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import ProductTable from './components/ProductTable'

function App() {
  const [count, setCount] = useState(0)

  return (
    <>
      <ProductTable></ProductTable>
    </>
  )
}

export default App
