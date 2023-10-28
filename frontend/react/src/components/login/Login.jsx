import {
    Button,
    Checkbox,
    Flex,
    Text,
    FormControl,
    FormLabel,
    Heading,
    Input,
    Stack,
    Image, Box, Alert, AlertIcon,
} from '@chakra-ui/react'
import {Form, Formik, useField} from "formik";

import * as Yup from 'yup';
import {useAuth} from "../context/AuthContext.jsx";
import {errorNotification} from "../../services/notification.js";
import {useNavigate} from "react-router-dom";
import {useEffect} from "react";

const MyTextInput = ({ label, ...props }) => {
    // useField() returns [formik.getFieldProps(), formik.getFieldMeta()]
    // which we can spread on <input>. We can use field meta to show an error
    // message if the field is invalid and it has been touched (i.e. visited)
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={2}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};



const LoginForm = () => {

    const { login } = useAuth();

    const navigate = useNavigate();

    return (
        <Formik
            validateOnMount={true}
            validationSchema={
                Yup.object({
                    username: Yup.string()
                        .email('Must be valid email')
                        .required('Required'),
                    password: Yup.string()
                        .max(20, 'Must be 20 characters or less')
                        .required('Required'),
                })
            }
            initialValues={{username: '', password: ''}}
            onSubmit={(values, {setSubmitting})=>{
                setSubmitting(true);
                login(values).then(res => {
                    // navigate to dashboard
                    navigate("/dashboard");
                    console.log("Successfully logged in");

                }).catch(err => {
                    console.log("Login Error", err);
                    errorNotification(
                        err.code,
                        err.response.data.message
                    );
                }).finally(()=> {
                    setSubmitting(false);
                })
            }}>

            {({isValid, isSubmitting}) =>{
                return (
                <Form>
                    <Stack spacing={15}>
                        <MyTextInput
                            label="Email"
                            name="username"
                            type="text"
                        />

                        <MyTextInput
                            label="Password"
                            name="password"
                            type="password"
                        />
                        <Button
                            disabled={!isValid || isSubmitting}
                            type={"submit"}
                        >
                            Login
                        </Button>
                    </Stack>
                </Form>
                )
            }}

        </Formik>
    )
    
}

const Login = () => {

    const { customer } = useAuth();
    const navigate= useNavigate();

    // if customer exists, already logged in, navigate to dashboard
    useEffect(()=>{
        if (customer) {
            navigate("/dashboard");
        }
    })

    return (
        <Stack minH={'100vh'} direction={{ base: 'column', md: 'row' }}>
            <Flex p={8} flex={1} align={'center'} justify={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Heading fontSize={'2xl'} mb={15}>Sign in to your account</Heading>
                    <LoginForm />
                </Stack>
            </Flex>
            <Flex flex={1}>
                <Image
                    alt={'Login Image'}
                    objectFit={'cover'}
                    src={
                        'https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1352&q=80'
                    }
                />
            </Flex>
        </Stack>
    )
}

export default Login;