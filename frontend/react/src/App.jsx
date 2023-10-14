import { Button, ButtonGroup } from '@chakra-ui/react'
import SidebarWithHeader from './shared/SideBar.jsx'

// Main App
const App = () => {
    return (
        <SidebarWithHeader>
            <Button colorScheme='teal' variant='solid'>Click Me</Button>
        </SidebarWithHeader>
    )
}

export default App
