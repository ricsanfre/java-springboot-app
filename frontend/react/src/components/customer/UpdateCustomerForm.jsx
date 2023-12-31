// React forms with Formik https://formik.org/

import {Formik, Form, useField} from 'formik';
import * as Yup from 'yup';
import {Alert, AlertIcon, Box, Button, FormLabel, Image, Input, Select, Stack, VStack} from "@chakra-ui/react";
import {
    customerProfilePictureUrl,
    saveCustomer,
    updateCustomer,
    uploadCustomerProfileImage
} from "../../services/client.js";
import {errorNotification, successNotification} from "../../services/notification.js";
import {useCallback} from "react";
import {useDropzone} from "react-dropzone";

const MyTextInput = ({label, ...props}) => {
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

const MySelect = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Select {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={2}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const MyCheckbox = ({children, ...props}) => {
    // React treats radios and checkbox inputs differently from other input types: select and textarea.
    // Formik does this too! When you specify `type` to useField(), it will
    // return the correct bag of props for you -- a `checked` prop will be included
    // in `field` alongside `name`, `value`, `onChange`, and `onBlur`
    const [field, meta] = useField({...props, type: 'checkbox'});
    return (
        <div>
            <label className="checkbox-input">
                <input type="checkbox" {...field} {...props} />
                {children}
            </label>
            {meta.touched && meta.error ? (
                <div className="error">{meta.error}</div>
            ) : null}
        </div>
    );
};

// react-dropzone react component
// https://react-dropzone.js.org/

const MyDropzone = ({id, fetchCustomers}) => {
    const onDrop = useCallback(acceptedFiles => {
        // Do something with the files
        const formData = new FormData();
        formData.append("file", acceptedFiles[0]);
        uploadCustomerProfileImage(id, formData)
            .then((res)=>{
                successNotification("Success", "Profile image successfully uploaded");
                fetchCustomers();
            }).catch((err) => {
                errorNotification("Fail", "Profile image upload fails");

        });
    }, [])
    const {getRootProps, getInputProps, isDragActive} = useDropzone({onDrop})

    return (
        <Box {...getRootProps()}
             w={'100%'}
             textAlign={'center'}
             border={'dashed'}
             borderColor={'gray.200'}
             borderRadius={'3xl'}
             p={6}
             rounded={'md'}
        >
            <input {...getInputProps()} />
            {
                isDragActive ?
                    <p>Drop the profile image here ...</p> :
                    <p>Drag 'n' drop image here, or click to select image</p>
            }
        </Box>
    )
}

// And now we can use these
const UpdateCustomerForm = ({initialValues, fetchCustomers}) => {
    return (
        <>
            <VStack spacing={'5'} mb={'5'}>
                <Image
                    borderRadius={'full'}
                    boxSize={'150px'}
                    objectFit={'cover'}
                    src={customerProfilePictureUrl(initialValues.id)}
                />
                <MyDropzone
                    id={initialValues.id}
                    fetchCustomers={fetchCustomers}
                />
            </VStack>
            <Formik
                initialValues={{
                    id: `${initialValues.id}`,
                    name: `${initialValues.name}`,
                    password: '',
                    updatePassword: false,
                    email: `${initialValues.email}`,
                    age: `${initialValues.age}`,
                    gender: `${initialValues.gender}`,
                }}
                validationSchema={Yup.object({
                    name: Yup.string()
                        .max(15, 'Must be 15 characters or less')
                        .required('Required'),
                    updatePassword: Yup.boolean()
                        .required('Required')
                        .oneOf([true, false], 'Select if you want to update the password.'),
                    password: Yup.string()
                        .max(20, 'Must be 20 characters or less')
                        .when('updatePassword', {
                            is: true,
                            then: () => Yup.string()
                                .max(20, 'Must be 20 characters or less')
                                .required('Required')
                        }),
                    email: Yup.string()
                        .email('Invalid email address')
                        .required('Required'),
                    age: Yup.number()
                        .min(15, 'Must be at least 15')
                        .max(100, 'Must be less than 100')
                        .required('Required'),
                    gender: Yup.string()
                        .oneOf(
                            ['FEMALE', 'MALE'],
                            'Invalid Gender Type'
                        )
                        .required('Required'),
                })}
                onSubmit={(values, {setSubmitting}) => {
                    setSubmitting(true);
                    if (!values.updatePassword) {
                        delete values.password;
                    }
                    updateCustomer(values)
                        .then(res => {
                            console.log(res);
                            successNotification(
                                "Customer Updated",
                                `${values.name} was successfully updated`);
                            fetchCustomers();
                        }).catch(err => {
                        console.log(err);
                        errorNotification(
                            err.code,
                            err.response.data.message
                        )
                    }).finally(() => {
                        setSubmitting(false);
                    })
                }}
            >
                {({isValid, isSubmitting, dirty}) => (
                    <Form>
                        <Stack spacing={"24px"}>
                            <MyTextInput
                                label="Name"
                                name="name"
                                type="text"
                            />

                            <MyTextInput
                                label="Password"
                                name="password"
                                type="password"
                            />

                            <MyCheckbox name="updatePassword">
                                Select to update password
                            </MyCheckbox>

                            <MyTextInput
                                label="Email Address"
                                name="email"
                                type="email"
                            />

                            <MyTextInput
                                label="Age"
                                name="age"
                                type="number"
                            />

                            <MySelect label="Gender" name="gender">
                                <option value="">Select a gender</option>
                                <option value="FEMALE">Female</option>
                                <option value="MALE">Male</option>
                            </MySelect>

                            <Button isDisabled={!(isValid && dirty) || isSubmitting} type="submit">Submit</Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};

export default UpdateCustomerForm;