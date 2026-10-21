import * as functions from "firebase-functions/v1";
import * as admin from "firebase-admin";

admin.initializeApp();

// Disabled until provider signatures, reconciliation and replay protection exist.
export const verifyPaymentWebhook = functions.https.onRequest((_req, res) => {
  res.status(503).json({error: "Payment verification is not configured."});
});

export const onMatchStatusChanged = functions.firestore
  .document("matches/{matchId}")
  .onUpdate(async (change) => {
    const before = change.before.data();
    const after = change.after.data();
    if (!before.isLive && after.isLive) {
      await admin.messaging().send({
        notification: {
          title: "LIVE NOW: " + (after.matchTitle || "ETFC"),
          body: "The fight has started. Open ETFC Stream to watch.",
        },
        topic: "live_events",
      });
    }
  });

// Bootstrap the first admin using the Admin SDK in a trusted environment.
export const claimAdminRole = functions.https.onCall(async (data, context) => {
  if (!context.auth || !context.auth.token.email_verified) {
    throw new functions.https.HttpsError("unauthenticated", "A verified account is required.");
  }
  const email = context.auth.token.email;
  const inviteCode = data?.inviteCode;
  if (!email || typeof inviteCode !== "string" || !inviteCode.trim()) {
    throw new functions.https.HttpsError("invalid-argument", "An invite code is required.");
  }
  const inviteRef = admin.firestore().collection("admin_invites").doc(email);
  await admin.firestore().runTransaction(async (transaction) => {
    const invite = await transaction.get(inviteRef);
    if (!invite.exists || invite.data()?.code !== inviteCode) {
      throw new functions.https.HttpsError("permission-denied", "Invalid invite code.");
    }
    transaction.set(admin.firestore().collection("users").doc(context.auth!.uid),
      {role: "admin"}, {merge: true});
    transaction.delete(inviteRef);
  });
  const user = await admin.auth().getUser(context.auth.uid);
  await admin.auth().setCustomUserClaims(context.auth.uid, {...user.customClaims, admin: true});
  return {success: true, message: "Administrator granted. Refresh your ID token."};
});
