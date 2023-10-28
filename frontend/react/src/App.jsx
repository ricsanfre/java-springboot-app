import {Wrap, WrapItem, Spinner, Text} from '@chakra-ui/react'
import SidebarWithHeader from './components/shared/SideBar.jsx'
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/customer/Card.jsx";
import CreateDrawerForm from "./components/customer/CreateDrawerForm.jsx";
import {errorNotification} from "./services/notification.js";

// Main App
const App = () => {

    const [customers, setCustomers] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [err, setError] = useState("");
    const fetchCustomers = () =>{
        setIsLoading(true);
        getCustomers().then(res => {
            setCustomers(res.data);
            console.log(res)
        }).catch(err => {
            setError(err.response.data.message);
            errorNotification(
                err.code,
                err.response.data.message
            );
        }).finally(() => {
            setIsLoading(false);
        })
    }

    useEffect(() => {
        fetchCustomers();
    }, []);

    if (isLoading) {
        return (
            <SidebarWithHeader>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SidebarWithHeader>
        )
    }

    if (err) {
        return (
            <SidebarWithHeader>
                <CreateDrawerForm fetchCustomers={fetchCustomers}/>
                <Text mt={5}>Ooops there was an error</Text>
            </SidebarWithHeader>
        )
    }
    if (customers.length <= 0) {
        return (
            <SidebarWithHeader>
                <CreateDrawerForm fetchCustomers={fetchCustomers}/>
                <Text mt={5}>No customers found</Text>
            </SidebarWithHeader>
        )
    }

    return (
        <SidebarWithHeader>
            <CreateDrawerForm fetchCustomers={fetchCustomers}/>
            <Wrap spacing='30px' justify='center'>
                {customers.map((customer, index) => (
                    <WrapItem key={index}>
                        <CardWithImage fetchCustomers={fetchCustomers} {...customer}/>
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    )
}

export default App
