// src/App.tsx
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { useAuth } from "./context/AuthContext";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import FeedPage from "./pages/FeedPage";
import UserSearchPage from "./pages/UserSearchPage";
import FriendsPage from "./pages/FriendsPage";
import EventsPage from './pages/EventsPage';
import ProfilePage from './pages/ProfilePage';
import Navbar from "./components/Navbar";
import GroupListPage from "./pages/GroupListPage";
import CreateGroupPage from "./pages/CreateGroupPage";
import GroupDetailPage from "./pages/GroupDetailPage";
import EditGroupPage from "./pages/EditGroupPage";
import ChatPage from "./pages/ChatPage";
import ChatListPage from "./pages/ChatListPage";

function App() {

  const {token} = useAuth()


  return (
    <Router>
      <div>
        <Navbar />
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route
            path="/feed"
            element={token ? <FeedPage /> : <Navigate to="/login" />}
          />
          <Route path="/users" element={<UserSearchPage />} />
          <Route path="/userpage" element={<ProfilePage />} />
          <Route path="/events" element={<EventsPage />} />
          <Route path="/friends" element={<FriendsPage />} />
          <Route path="/users/:id" element={<ProfilePage />} />
          <Route path="/groups" element={<GroupListPage />} />
        <Route path="/groups/create" element={<CreateGroupPage />} />
        <Route path="/groups/:id" element={<GroupDetailPage />} />
        <Route path="/groups/:id/edit" element={<EditGroupPage />} />
        <Route path="/chats" element={token ? <ChatListPage /> : <Navigate to="/login" />} />
        <Route path="/chats/:id" element={token ? <ChatPage /> : <Navigate to="/login" />} />
        <Route path="/" element={<Navigate to={token ? "/feed" : "/login"} />} />
          {/* <Route path="/friends/add/:id" element={<AddFriendPage />} />
          <Route path="/chats/new/:id" element={<NewChatPage />} /> */}
        </Routes>
      </div>
    </Router>
  );
}

export default App;
