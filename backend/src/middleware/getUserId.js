const getUserId = (req, res, next) => {
    if(req.headers.authorization && req.headers.authorization.split(' ')[0] === 'Bearer') {
        req.userId = req.headers.authorization.split(' ')[1];
    } else {
        res.sendStatus(401);
    }
    next();
};

export { getUserId };