// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp();

// [Function purpose]
// Runs at 12AM ET every Sunday
exports.scheduledFunctionCronTab = functions.pubsub.schedule('0 19 * * 6').timeZone('America/New_York').onRun((context) => {
  console.log('Testing a scheduled function');
  // Input functionality
  return null;
});