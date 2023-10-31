import express from 'express';

import { 
    getUser, 
    createUser, 
    updateUser, 
    deleteUser,
    addFavouriteCourse,
    getFavouriteCourses,
    removeFavouriteCourse,
    getCourseKeywords,
    getRecommendedCourses
} from '../controllers/userController.js';

const router = express.Router();

/* ChatGPT usage: No */

// Get user by ID
router.get('/', getUser);

// Create user
router.post('/', createUser);

// Post user by ID
router.put('/', updateUser);

// Delete user by ID
router.delete('/', deleteUser);

// Get favourite courses
router.get('/favourite', getFavouriteCourses);

// Add favourite course
router.post('/favourite', addFavouriteCourse);

// Remove favourite course
router.delete('/favourite/:course_id', removeFavouriteCourse);

// Get course keywords
router.get('/courseKeywords', getCourseKeywords);

/* req must contain a json body of keywords like {"keywords" : ["power", "wires"]} */
router.get('/recommendedCourses', getRecommendedCourses);

export default router;