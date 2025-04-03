import React, { useState, useEffect } from 'react';
import UserService from '../service/UserService';
import { Link, useNavigate } from 'react-router-dom';

function ProfilePage() {
    const [profileInfo, setProfileInfo] = useState(null);
    const navigate = useNavigate();  // Fix: Redirect if not authenticated

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            console.error("❌ No token found in localStorage");
            alert("You are not authenticated! Please log in again.");
            navigate('/login');  // Redirect to login page
            return;
        }

        fetchProfileInfo(token);
    }, []);

    const fetchProfileInfo = async (token) => {
        try {
            console.log("📢 Fetching profile...");
            const response = await UserService.getYourProfile(token);
            setProfileInfo(response);  // Fix: Store user data properly
        } catch (error) {
            console.error('❌ Error fetching profile information:', error);
            alert('Failed to load profile data. Please try again.');
        }
    };

    if (!profileInfo) return <p>Loading...</p>;

    return (
        <div className="profile-page-container">
            <h2>Profile Information</h2>
            <p><strong>Name:</strong> {profileInfo.name}</p>
            <p><strong>Email:</strong> {profileInfo.email}</p>
            <p><strong>City:</strong> {profileInfo.city || "N/A"}</p>
            {profileInfo.role === "ADMIN" && (
                <Link to={`/update-user/${profileInfo.id}`}>
                    <button>Update This Profile</button>
                </Link>
            )}
        </div>
    );
}

export default ProfilePage;
