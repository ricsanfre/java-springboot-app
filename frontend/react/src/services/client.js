import axios from 'axios';

export const getCustomers = async()=> {
    try {
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customer`)
        
    } catch (e) {
        throw e;
    }
};

export const saveCustomer = async(customer)=> {
    try {
        return await axios.post(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customer`,
            customer)

    } catch (e) {
        throw e;
    }
};

export const deleteCustomer = async(customerId)=> {
    try {
        return await axios.delete(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customer/${customerId}`);

    } catch (e) {
        throw e;
    }
};