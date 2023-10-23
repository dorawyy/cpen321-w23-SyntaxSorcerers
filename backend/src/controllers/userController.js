import User from '../models/User.js';
import UserCourses from '../models/UserCourses.js';

async function getUser(req, res) {
    const model = new User();
    try {
        const user = await model.getUser(req.userId);
        return res.json(user);
    } catch (error) {
        return res.status(500).send(error.message);
    }
}

async function createUser(req, res) {
    const model = new User();
    try {
        const user = {
            userId: req.userId,
            email: req.body.email || "",
            year_level : req.body.year_level || "",
            major : req.body.major || "",
            name : req.body.name || ""
        };
        await model.createUser(user);
        return res.json(user);
    } catch (error) {
        return res.status(500).send(error.message);
    }
}

async function updateUser(req, res) {
    const model = new User();
    try {
        await model.updateUser(req.userId, req.body);
        res.send('User updated');
    } catch (error) {  
        return res.status(500).send(error.message);
    }
}

async function deleteUser(req, res) {
    const model = new User();
    try {
        await model.deleteUser(req.userId);
        res.send('User deleted');
    } catch (error) {
        return res.status(500).send(error.message);
    }
}

async function addFavouriteCourse(req, res) {
    const model = new UserCourses();
    try {
        const courses = await model.addUserCourse(req.userId, req.body.courseId);
        return res.send('Course added');
    } catch (error) {
        return res.status(500).send(error.message);
    }
};

async function getFavouriteCourses(req, res) {
    const model = new UserCourses();
    try {
        const courses = await model.getUserCourses(req.userId);
        return res.json(courses);
    } catch (error) {
        return res.status(500).send(error.message);
    }
}

async function removeFavouriteCourse(req, res) {
    const model = new UserCourses();
    const course_id = req.params.course_id;
    try {
        await model.removeUserCourse(req.userId, course_id);
        return res.send('Course removed');
    } catch (error) {
        return res.status(500).send(error.message);
    }
}

export { getUser, createUser, updateUser, deleteUser, addFavouriteCourse, getFavouriteCourses, removeFavouriteCourse };