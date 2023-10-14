import {Wrap, WrapItem, Spinner, Text} from '@chakra-ui/react'
import SidebarWithHeader from './components/shared/SideBar.jsx'
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/Card.jsx";

// Main App
const App = () => {

    const [customers, setCustomers] = useState([]);
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        setIsLoading(true);
        getCustomers().then(res => {
            setCustomers(res.data);
            console.log(res)
        }).catch(err => {
            console.log(err)
        }).finally(() => {
            setIsLoading(false);
        })
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

    if (customers.length <= 0) {
        return (
            <SidebarWithHeader>
                <Text>No customers found</Text>
            </SidebarWithHeader>
        )
    }

    return (
        <SidebarWithHeader>
            <Wrap spacing='30px' justify='center'>
                {customers.map((customer, index) => (
                    <WrapItem key={index}>
                        <CardWithImage {...customer}/>
                    </WrapItem>
                ))}
            </Wrap>
        </SidebarWithHeader>
    )
}

export default App
