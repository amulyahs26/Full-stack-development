const express = require('express');
const jsonfile = require('jsonfile');
const path = require('path');

const app = express();
app.set('view engine', 'ejs');
app.use(express.static('public'));
app.use(express.urlencoded({ extended: true }));

const airlineFile = './data/airlines.json';
const bookingFile = './data/bookings.json';

// Home route
app.get('/', (req, res) => {
    res.render('home');
});

// GET Add Airline Page
app.get('/add', (req, res) => {
    res.render('add');
});

// POST Add Airline
app.post('/add', (req, res) => {
    const airline = req.body.airline.trim();
    if (!airline) return res.redirect('/add');
    jsonfile.readFile(airlineFile, (err, airlines = []) => {
        airlines.push(airline);
        jsonfile.writeFile(airlineFile, airlines, { spaces: 2 }, () => {
            res.redirect('/add');
        });
    });
});

// GET Book Ticket Page
app.get('/book', (req, res) => {
    jsonfile.readFile(airlineFile, (err, airlines = []) => {
        res.render('book', { airlines });
    });
});

// POST Book Ticket
app.post('/book', (req, res) => {
    const { airline, source, destination, date, customerId } = req.body;
    const booking = { airline, source, destination, date, customerId };
    jsonfile.readFile(bookingFile, (err, bookings = []) => {
        bookings.push(booking);
        jsonfile.writeFile(bookingFile, bookings, { spaces: 2 }, () => {
            res.redirect('/book');
        });
    });
});

// GET Booking Details
app.get('/details', (req, res) => {
    jsonfile.readFile(bookingFile, (err, bookings = []) => {
        res.render('details', { bookings });
    });
});

app.listen(3000, () => console.log('http://localhost:3000'));
