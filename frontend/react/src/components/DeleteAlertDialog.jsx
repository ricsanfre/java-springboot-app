import {
    AlertDialog,
    AlertDialogBody,
    AlertDialogContent,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogOverlay,
    Button, useDisclosure
} from "@chakra-ui/react";
import React from "react";
import {deleteCustomer} from "../services/client.js";
import {errorNotification, successNotification} from "../services/notification.js";

function DeleteAlertDialog({id, fetchCustomers}) {
    const {isOpen, onOpen, onClose} = useDisclosure()
    const cancelRef = React.useRef()


    return (
        <>
            <Button colorScheme='red' onClick={onOpen}>
                Delete Customer
            </Button>

            <AlertDialog
                isOpen={isOpen}
                leastDestructiveRef={cancelRef}
                onClose={onClose}
            >
                <AlertDialogOverlay>
                    <AlertDialogContent>
                        <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                            Delete Customer
                        </AlertDialogHeader>

                        <AlertDialogBody>
                            Are you sure? You can't undo this action afterwards.
                        </AlertDialogBody>

                        <AlertDialogFooter>
                            <Button ref={cancelRef} onClick={onClose}>
                                Cancel
                            </Button>
                            <Button
                                colorScheme='red'
                                ml={3}
                                onClick={() => {
                                   deleteCustomer(id)
                                    .then(res => {
                                        console.log(res);
                                        successNotification(
                                            "Customer Delete",
                                            `${id} was successfully deleted`);
                                        fetchCustomers();
                                    }).catch(err => {
                                    console.log(err);
                                    errorNotification(
                                        err.code,
                                        err.response.data.message
                                    )}).finally(()=> { onClose()} )
                                }}
                            >
                                Delete
                            </Button>
                        </AlertDialogFooter>
                    </AlertDialogContent>
                </AlertDialogOverlay>
            </AlertDialog>
        </>
    )
}

export default DeleteAlertDialog;