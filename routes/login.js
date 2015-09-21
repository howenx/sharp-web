var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/login', function(req, res, next) {
	res.render('login.html');
});
router.get('/regist', function(req, res, next) {
	res.render('regist.html');
});
router.get('/detail', function(req, res, next) {
	var pics = [];
	for(var i=1;i<44;i++){
		pics.push(i);
	}
	var slides = [];
	for(var i=0;i<5;i++){
		slides.push(i);
	}
	res.render("detail.html", {
		pics: pics,
		slides: slides
	});
});
module.exports = router;
