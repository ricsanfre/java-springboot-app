import SidebarWithHeader from './components/shared/SideBar.jsx'

import {
    Text
} from '@chakra-ui/react'


// Home
const Settings = () => {
    return (
        <SidebarWithHeader>
            <Text fontSize={"6xl"}>Settings</Text>
        </SidebarWithHeader>
    )
}

export default Settings
