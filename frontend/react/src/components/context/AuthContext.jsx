import {
    createContext,
    useContext,
    useEffect,
    useState
} from "react";
import {login as performLogin} from "../../services/client.js";
import {jwtDecode} from "jwt-decode";



const AuthContext= createContext({});

const AuthProvider = ({children}) => {

    const [customer, setCustomer] = useState(null);

    useEffect(()=>{
        let token=localStorage.getItem("access_token");
        if(token){
            token = jwtDecode(token);
            setCustomer({
                username: token.sub,
                roles: token.scopes
            })
        }
    }, [])

    const login = async (userNameAndPassword)=> {
        return new Promise((resolve, reject)=> {
            performLogin(userNameAndPassword).then(res => {
                // Get jwt token from header
                const jwtToken = res.headers["authorization"]
                // save token into local storage
                localStorage.setItem("access_token", jwtToken);
                console.log(jwtToken)
                const decodedToken = jwtDecode(jwtToken);
                setCustomer({
                    username: decodedToken.sub,
                    roles: decodedToken.scopes
                })
                //setCustomer({
                //    ...res.data.customerDTO
                //});
                resolve(res);
            }).catch(err => {
                reject(err);
            })
        })
    }

    const logout = () => {
        localStorage.removeItem("access_token");
        setCustomer(null);
    }

    const isUserAuthenticated = () => {
        const token = localStorage.getItem("access_token");
        if (!token) {
            return false;
        }
        // extract expiration date from jwt token
        const decodedToken = jwtDecode(token);
        if(Date.now() > decodedToken.exp * 1000) {
            // token has expired
            logout();
            return false;
        }
        return true;
    }

    return (
        <AuthContext.Provider value={{
            customer,
            login,
            logout,
            isUserAuthenticated
        }}>
            {children}
        </AuthContext.Provider>
    )
}
export const useAuth = () => useContext(AuthContext);

export default AuthProvider;