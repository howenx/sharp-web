var express = require('express');
var router = express.Router();

/* home page render. */
router.get('/', function(req, res, next) {
  res.render('index.html');
});

module.exports = router;