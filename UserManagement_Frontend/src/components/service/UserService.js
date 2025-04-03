import axios from "axios";

class UserService{
    static BASE_URL = "http://localhost:1010"

    static async login(email, password){
        try{
            const response = await axios.post(`${UserService.BASE_URL}/auth/login`, {email, password})
            return response.data;

        }catch(err){
            throw err;
        }
    }

    static async register(userData, token){
        try{
            const response = await axios.post(`${UserService.BASE_URL}/auth/register`, userData, 
            {
                headers: {Authorization: `Bearer ${token}`}
            })
            return response.data;
        }catch(err){
            throw err;
        }
    }

    static async getAllUsers(token){
        try{
            const response = await axios.get(`${UserService.BASE_URL}/admin/get-all-users`, 
            {
                headers: {Authorization: `Bearer ${token}`}
            })
            return response.data;
        }catch(err){
            throw err;
        }
    }



    static async getYourProfile(token){
        try {
            if (!token) {
                throw new Error("No token provided");
            }
    
            console.log("📢 Fetching profile with token:", token);
    
            const response = await axios.get(`${UserService.BASE_URL}/adminuser/get-profile`, {
                headers: { Authorization: `Bearer ${token}` }
            });
    
            if (!response.data || !response.data.ourUsers) {
                throw new Error("Invalid response structure");
            }
    
            console.log("✅ Profile API Response:", response.data.ourUsers);
            return response.data.ourUsers;  // Fix: return only the user data
        } catch (err) {
            console.error("❌ Error fetching profile:", err.response ? err.response.data : err.message);
            throw err;
        }
    }
    
    
    

    static async getUserById(userId, token){
        try{
            const response = await axios.get(`${UserService.BASE_URL}/admin/get-users/${userId}`, 
            {
                headers: {Authorization: `Bearer ${token}`}
            })
            return response.data;
        }catch(err){
            throw err;
        }
    }

    static async deleteUser(userId, token){
        try{
            const response = await axios.delete(`${UserService.BASE_URL}/admin/delete/${userId}`, 
            {
                headers: {Authorization: `Bearer ${token}`}
            })
            return response.data;
        }catch(err){
            throw err;
        }
    }


    static async updateUser(userId, userData, token){
        try{
            const response = await axios.put(`${UserService.BASE_URL}/admin/update/${userId}`, userData,
            {
                headers: {Authorization: `Bearer ${token}`}
            })
            return response.data;
        }catch(err){
            throw err;
        }
    }

    /**AUTHENTICATION CHECKER */
    static logout(){
        localStorage.removeItem('token')
        localStorage.removeItem('role')
    }

    static isAuthenticated(){
        const token = localStorage.getItem('token')
        return !!token
    }

    static isAdmin(){
        const role = localStorage.getItem('role')
        return role === 'ADMIN'
    }

    static isUser(){
        const role = localStorage.getItem('role')
        return role === 'USER'
    }

    static adminOnly(){
        return this.isAuthenticated() && this.isAdmin();
    }

}

export default UserService;