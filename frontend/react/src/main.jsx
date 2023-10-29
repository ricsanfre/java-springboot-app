import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './Customer.jsx'
// Import `ChakraProvider` component
import {ChakraProvider, Text} from '@chakra-ui/react'

import { createStandaloneToast } from '@chakra-ui/react'

// Import react-router
import { createBrowserRouter, RouterProvider} from "react-router-dom";

import './index.css'
import Login, {Register} from "./components/login/Login.jsx";

import AuthProvider from "./components/context/AuthContext.jsx";
import ProtectedRoute from "./components/shared/ProtectedRoute.jsx";
import Customer from "./Customer.jsx";
import Home from "./Home.jsx";
import Settings from "./Settings.jsx";


const router = createBrowserRouter([
    {
        path: "/",
        element: <Login />
    },
    {
        path: "register",
        element: <Register />
    },
    {
        path: "dashboard",
        element: <ProtectedRoute><Home /></ProtectedRoute>
    },
    {
        path: "dashboard/customers",
        element: <ProtectedRoute><Customer /></ProtectedRoute>
    },
    {
        path: "dashboard/settings",
        element: <ProtectedRoute><Settings /></ProtectedRoute>
    },

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
