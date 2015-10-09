/******** reference modules ***********/
var favicon = require('serve-favicon');
var morgan = require('morgan');
var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var dateformat = require('console-stamp/node_modules/dateformat');
var ip = require('ip');

/********** modules config *************/
var app = express();
var index = require('./routes/index');
var login = require('./routes/login');

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

/********* routers ******************/
app.use(express.static('public'));
app.use('/', index);
app.use('/', login);

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
	res.sendFile(process.cwd() + '/public/images/404.jpg');
});
/********* error handlers **********/
app.use(function(err, req, res, next) {
	if (err.code === 'ENOENT') {
		next();
	} else {
		res.status(500);
		res.sendFile(process.cwd() + '/public/images/500.jpg');
		console.error(colors.error(err.stack));
	}
});

/************* set port *******************/
app.set('port', process.env.PORT || 3333);

// var server = app.listen(app.get('port'), function() {
  console.log('> '+colors.grey('Time: '+dateformat(new Date(), 'yyyy-mm-dd HH:MM:ss TT'))+colors.gray('\tNodejs server listening on ' + colors.magenta(ip.address()+':'+process.env.PORT)));
  console.log(colors.cyan('\n····························Server Started····························\n'));
// });
url = 'http://'+ip.address()+':'+process.env.PORT;
module.exports = app;

