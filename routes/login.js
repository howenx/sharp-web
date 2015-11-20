var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/login', function(req, res, next) {
	res.render('login.html');
});

router.get('/list', function(req, res, next) {
	res.render('list.html');
});

router.get('/personal', function(req, res, next) {
	res.render('personal.html');
});

router.get('/shopping', function(req, res, next) {
	res.render('shopping.html');
});

router.get('/obligation', function(req, res, next) {
	res.render('obligation.html');
});

router.get('/orders', function(req, res, next) {
	res.render('orders.html');
});

router.get('/delivered', function(req, res, next) {
	res.render('delivered.html');
});

router.get('/pay', function(req, res, next) {
	res.render('pay.html');
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
