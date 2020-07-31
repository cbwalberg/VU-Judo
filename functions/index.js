// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp();

const db = admin.firestore();

// [Function purpose]
// Runs at 12AM ET every Sunday '0 0 * * 0'
// Using '*/5 * * * *' (every five minutes) for testing purposes
exports.scheduledFunctionCronTab = functions.pubsub.schedule('*/5 * * * *').timeZone('America/New_York').onRun((context) => {

  const queryRef = db.collection('users');
  var scoreArray = [], top3 = [];

  const getQuery = queryRef.get().then(query => {
    let docs = query.docs;
    for (let i=0; i<docs.length; i++) {
      scoreArray.push(docs[i].data());    
    }

    scoreArray.sort((a,b) => {return b.score - a.score});
    for (let i=0; i<3; i++) {
      top3.push(scoreArray[i])
      console.log(`Retrieved data: ${JSON.stringify(top3[i])}`);
    }

    return null;
  });

  let now = new Date();
  let month = now.getMonth() + 1;
  let leaderboardDocName = 'leaderboard/Week of ' + month + '-' + now.getDate().toString() + '-' + now.getFullYear().toString();
  console.log(leaderboardDocName);

  return null;
});