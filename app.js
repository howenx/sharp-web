/******** reference modules ***********/
var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var dateformat = require('console-stamp/node_modules/dateformat');

/********** modules config *************/
var app = express();
var index = require('./routes/index');

/**** routers config ****/
app.use('/', index);

/****** Global variable ************/
colors = require('colors');
colors.setTheme({
	silly: 'rainbow',
	input: 'grey',
	verbose: 'cyan',
	prompt: 'grey',
	info: 'green',
	data: 'grey',
	help: 'cyan',
	warn: 'yellow',
	debug: 'blue',
	error: 'red'
});

/*** view engine setup ****/
app.engine('html', require('ejs').renderFile);
app.set('view engine', 'html');
app.set("view options", {
	layout: false
});
app.set('views', path.join(__dirname, 'views'));
app.settings.env === 'dev';
app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));

/***** Log area *****/
morgan.token('date', function gedate(req) {
	var date = new Date();
	return dateformat(date, 'dd/mm/yyyy HH:MM:ss TT');
})
app.use(morgan(':remote-addr - :remote-user :date :method :url :status :res[content-length] ":referrer" ":user-agent'));

/**** cookie ****/
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
	extended: false
}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  var err = new Error('Not Found');
  err.status = 404;
  next(err);
});

// error handlers
/********* catch 404 and forward to error handler **********/
app.use(function(req, res, next) {
	var err = new Error('Not Found');
	err.status = 404;
	res.status(404);
	res.sendFile(process.cwd() + '/images/404.jpg');
});
/********* error handlers **********/
app.use(function(err, req, res, next) {
	if (err.code === 'ENOENT') {
		next();
	} else {
		res.status(500);
		res.sendFile(process.cwd() + '/images/500.jpg');
		console.error(colors.error(err.stack));
	}
});

module.exports = app;
