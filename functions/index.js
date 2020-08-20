// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
const { _scheduleWithOptions } = require('firebase-functions/lib/providers/pubsub');
admin.initializeApp();

const db = admin.firestore();

// Runs at 12AM ET every Sunday '0 0 * * 0'
exports.weeklyScoreReset = functions.pubsub.schedule('0 0 * * 0').timeZone('America/New_York').onRun((context) => {
  return setLeaderboardHistory();
});

// Record all user info in the users subcollection within a weekly doc of leaderboard_history
function setLeaderboardHistory() {
  const usersQuery = db.collection('users').get().then(query => {
    let docs = query.docs;

    // Get today's date
    let est = new Date().toLocaleString("en-US", {timeZone: "America/New_York"});
    let now = new Date(est); 
    let leaderboardDocName = 'Week of ' + (now.getMonth()+1).toString() + '-' + now.getDate().toString() + '-' + now.getFullYear().toString();

    for (let i=0; i<docs.length; i++) {
      db.doc('leaderboard_history/'+leaderboardDocName+'/users/'+docs[i].id).set(docs[i].data(), {merge : true});
    }

    return docs;

  }).then(() => {
    return resetUserScores();
  });

  return usersQuery;
}

// Reset all user scores to 0
function resetUserScores() {
  const usersQuery = db.collection('users').get().then(query => {
    let docs = query.docs;
  
    // Update all user scores to zero
    for (let i=0; i<docs.length; i++) {
      db.doc('users/'+docs[i].id).update({score : 0});
    }
  
    return docs;
  });

  return usersQuery;
}