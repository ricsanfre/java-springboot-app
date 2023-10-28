import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
// Import `ChakraProvider` component
import { ChakraProvider } from '@chakra-ui/react'

import { createStandaloneToast } from '@chakra-ui/react'

// Import react-router
import { createBrowserRouter, RouterProvider} from "react-router-dom";

import './index.css'
import Login from "./components/login/Login.jsx";

import AuthProvider from "./components/context/AuthContext.jsx";
import ProtectedRoute from "./components/shared/ProtectedRoute.jsx";

const router = createBrowserRouter([
    {
        path: "/",
        element: <Login />
    },
    {
        path: "dashboard",
        element: <ProtectedRoute><App /></ProtectedRoute>
    }
])

const { ToastContainer, toast } = createStandaloneToast()

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <ChakraProvider>
      <AuthProvider>
          <RouterProvider router={router} />
      </AuthProvider>
      <ToastContainer />
    </ChakraProvider>
  </React.StrictMode>,
)
