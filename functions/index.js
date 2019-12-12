'use strict'


const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


exports.pushNotification = functions.database.ref('/Notification/{receiver_id}/{job_id}/caseID').onWrite((data, context) =>
{
	
	const receiver_id = context.params.receiver_id;
	const notification_id = context.params.notification_id;
	const job_id   = context.params.job_id;
	const caseID   = data.after.val();
	console.log('Start');
    console.log('receiverID : ' + receiver_id);
	console.log('Job ID : ' + job_id);
	console.log('Case ID : ' + caseID);

	console.log('We have a notification to send to :' , receiver_id);


	if (!data.after.val()) 
	{
		console.log('A notification has been deleted :' , notification_id);
		return null;
	}

	const DeviceToken = admin.database().ref(`/User/${receiver_id}/fcmtoken`).once('value');
	return DeviceToken.then(result => 
	{
		const token_id = result.val();
		console.log("Token ID: " + token_id);

		const payload = 
		{
			notification:
			{
				title: "New Job Request",
				body: 'JobID: ' +  caseID +  ' has been assigned to you.',
				icon: "default"

			}
		};

		return admin.messaging().sendToDevice(token_id, payload)
		.then(response => 
			{
				console.log('This was a notification feature.');
			});
	});
});

exports.pushUpdateNotification = functions.database.ref('/Job/{job_id}').onWrite((data, context) =>
{
	
	const job_id   = context.params.job_id;
	const dataChange = data.after.val();
	const beforeData = data.before.val(); 
	const createdBy = dataChange.createdBy;
	const assigned  = dataChange.assigned;
	const caseID  = dataChange.caseID;
	const status = dataChange.status;
    const BeforeStatus = beforeData.status;

	console.log('Start');
	console.log('Job ID : ' + job_id);
	console.log('created : ' + createdBy);
	console.log('assigned : ' + assigned);
	console.log('After Job Status : ' + status);
	console.log('Before Job Status : ' + BeforeStatus);


	if (!data.after.val()) 
	{
		console.log('A notification has been deleted .');
	}

	if (BeforeStatus != status && status != null){
	const DeviceTokenRec = admin.database().ref(`/User/${createdBy}/fcmtoken`).once('value');
	return DeviceTokenRec.then(result => 
	{
		const token_recp_id = result.val();
		console.log("Recep token ID: " + token_recp_id);

		const payload = 
		{
			notification:
			{
				title: "Update to Job",
				body: 'Current status to Job: '  +  caseID + ' is ' +  status  + '.',
				icon: "default"

			}
		};

		return admin.messaging().sendToDevice(token_recp_id, payload)
		.then(response => 
			{
				console.log('This was a notification feature.');
			});
	});
	}
});

exports.pushUpdateNotificationToPorter = functions.database.ref('/Job/{job_id}').onWrite((data, context) =>
{
	
	const job_id   = context.params.job_id;
	const dataChange = data.after.val();

	const beforeData = data.before.val(); 
	const createdBy = dataChange.createdBy;
	const assigned  = dataChange.assigned;
	const caseID  = dataChange.caseID;
	const status = dataChange.status;
	const urgency = dataChange.jobUrgency;
	const type = dataChange.typeOfJob;
	const description = dataChange.description;
	
    const BeforeStatus = beforeData.status;
	const BeforeUrgency = beforeData.jobUrgency;
	const BeforeType = beforeData.typeOfJob;
	const BeforeDescription = beforeData.description;
	var urgencyLevel = [ 'Emergency', 'Urgent', 'Normal' ];

	console.log('Start');
	console.log('Job ID : ' + job_id);
	console.log('created : ' + createdBy);
	console.log('assigned : ' + assigned);
	console.log('After Job Status : ' + status);
	console.log('Before Job Status : ' + BeforeStatus);



	if (!data.after.val()) 
	{
		console.log('A notification has been deleted .');
	}
	
	if (BeforeStatus != status && status != null && status == 'Cancelled'){
	const DeviceTokenAssigned = admin.database().ref(`/User/${assigned}/fcmtoken`).once('value');
	return DeviceTokenAssigned.then(result => 
	{
		const token_porter_id = result.val();
		console.log("Porter Token ID: " + token_porter_id);

		const payload = 
		{
			notification:
			{
				title: "Update to Job",
				body: 'Current status to Job: '  +  caseID + ' is ' +  status  + '.',
				icon: "default"

			}
		};

		
		return admin.messaging().sendToDevice(token_porter_id, payload)
		.then(response => 
			{
				console.log('This was a notification feature for porter.');
			});
	});
	}
	
	if (BeforeStatus == status && status == 'Assigned'){
	const DeviceTokenAssigned = admin.database().ref(`/User/${assigned}/fcmtoken`).once('value');
	return DeviceTokenAssigned.then(result => 
	{
		const token_porter_id = result.val();
		console.log("Porter Token ID: " + token_porter_id);

		const payload = 
		{
			notification:
			{
				title: "You have been Assigned",
				body: 'Assigned: '  +  caseID + '.',
				icon: "default"

			}
		};

		
		return admin.messaging().sendToDevice(token_porter_id, payload)
		.then(response => 
			{
				console.log('This was a notification feature for porter.');
			});
	});
	}
	
	//change in urgency
	if(BeforeUrgency != urgency){
		const DeviceTokenAssigned = admin.database().ref(`/User/${assigned}/fcmtoken`).once('value');
	return DeviceTokenAssigned.then(result => 
	{
		const token_porter_id = result.val();
		console.log("Porter Token ID: " + token_porter_id);

		const payload = 
		{
			notification:
			{
				title: "Update to Job Urgency",
				body: 'Urgency to Job: '  +  caseID + ' changed to ' +  urgencyLevel[urgency-1]  + '.',
				icon: "default"

			}
		};

		
		return admin.messaging().sendToDevice(token_porter_id, payload)
		.then(response => 
			{
				console.log('This was a notification feature for porter.');
			});
	});
	}
	//change in Type
	if(BeforeType != type){
		const DeviceTokenAssigned = admin.database().ref(`/User/${assigned}/fcmtoken`).once('value');
	return DeviceTokenAssigned.then(result => 
	{
		const token_porter_id = result.val();
		console.log("Porter Token ID: " + token_porter_id);

		const payload = 
		{
			notification:
			{
				title: "Update to Job Type",
				body: 'Job: '  +  caseID + ' Type changed to ' +  type  + '.',
				icon: "default"

			}
		};

		
		return admin.messaging().sendToDevice(token_porter_id, payload)
		.then(response => 
			{
				console.log('This was a notification feature for porter.');
			});
	});
	}
});
	
exports.pushUpdateToLocationNotification = functions.database.ref('/Job/{job_id}').onWrite((data, context) =>
{
	const job_id   = context.params.job_id;
	const dataChange = data.after.val();
	const beforeData = data.before.val(); 
	
	const assigned  = dataChange.assigned;
	const createdBy = dataChange.createdBy;
	
	const fromLocation = dataChange.fromLocation;
	const toLocation  = dataChange.toLocation;

	const caseID  = dataChange.caseID;
    const BeforeToLocation = beforeData.toLocation;
	const BeforeFromLocation  =  beforeData.fromLocation;


	console.log('Start');
	console.log('Job ID : ' + job_id);



	if (!data.after.val()) 
	{
		console.log('A notification has been deleted .');
	}

	if ((BeforeToLocation != toLocation && BeforeToLocation != null)){
	const DeviceTokenRec = admin.database().ref(`/User/${assigned}/fcmtoken`).once('value');
	return DeviceTokenRec.then(result => 
	{
		const token_recp_id = result.val();

		const payload = 
		{
			notification:
			{
				title: "Update to Job Location(To)",
				body: 'Destination(To) for Job: '  +  caseID +' changed to : ' + toLocation,
				icon: "default"

			}
		};

		return admin.messaging().sendToDevice(token_recp_id, payload)
		.then(response => 
			{
				console.log('This was a notification feature.');
			});
	});
	}
	
	if ((fromLocation != BeforeFromLocation) && (BeforeFromLocation != null)){
	const DeviceTokenRec = admin.database().ref(`/User/${assigned}/fcmtoken`).once('value');
	return DeviceTokenRec.then(result => 
	{
		const token_recp_id = result.val();

		const payload = 
		{
			notification:
			{
				title: "Update to Job Location(From)",
				body: 'Destination(From) for Job: '  +  caseID +' changed to : ' + fromLocation,
				icon: "default"

			}
		};

		return admin.messaging().sendToDevice(token_recp_id, payload)
		.then(response => 
			{
				console.log('This was a notification feature.');
			});
	});
	}
	
	
	
});

exports.pushUpdateToLocationRecepNotification = functions.database.ref('/Job/{job_id}').onWrite((data, context) =>
{
	const job_id   = context.params.job_id;
	const dataChange = data.after.val();
	const beforeData = data.before.val(); 
	const createdBy = dataChange.createdBy;
	
	const fromLocation = dataChange.fromLocation;
	const toLocation  = dataChange.toLocation;


	const caseID  = dataChange.caseID;
    const BeforeToLocation = beforeData.toLocation;
	const BeforeFromLocation  =  beforeData.fromLocation;


	console.log('Start');
	console.log('Job ID : ' + job_id);




	if (!data.after.val()) 
	{
		console.log('A notification has been deleted .');
	}

  if ((BeforeToLocation != toLocation && BeforeToLocation != null)){
	const DeviceTokenRec = admin.database().ref(`/User/${createdBy}/fcmtoken`).once('value');
	return DeviceTokenRec.then(result => 
	{
		const token_recp_id = result.val();
		console.log('To Location1 : ' + toLocation);

		const payload = 
		{
			
			notification:
			{
				title: "Update to Job Location(To)",
				body: 'Destination(To) for Job: '  +  caseID +' changed to : ' + toLocation,
				icon: "default"

			}
		};

		return admin.messaging().sendToDevice(token_recp_id, payload)
		.then(response => 
			{
				console.log('This was a notification feature.');
			});
	});
	}
	
	if ((fromLocation != BeforeFromLocation) && (BeforeFromLocation != null)){
	const DeviceTokenRec = admin.database().ref(`/User/${createdBy}/fcmtoken`).once('value');
	return DeviceTokenRec.then(result => 
	{
		const token_recp_id = result.val();
		console.log('To Location2 : ' + toLocation);

		const payload = 
		{
			notification:
			{
				title: "Update to Job Location(From)",
				body: 'Destination(From) for Job: '  +  caseID +' changed to : ' + fromLocation,
				icon: "default"

			}
		};

		return admin.messaging().sendToDevice(token_recp_id, payload)
		.then(response => 
			{
				console.log('This was a notification feature.');
			});
	});
	}
	
	
	
});

exports.pushUpdateNotificationToAssignedRecep = functions.database.ref('/Job/{job_id}').onWrite((data, context) =>
{
	
	const job_id   = context.params.job_id;
	const dataChange = data.after.val();

	const beforeData = data.before.val(); 
	const createdBy = dataChange.createdBy;
	const assigned  = dataChange.assigned;
	const assignedBy  = dataChange.assignedBy;

	const caseID  = dataChange.caseID;
	const status = dataChange.status;
    const BeforeStatus = beforeData.status;


	console.log('Start');
	console.log('Job ID : ' + job_id);
	console.log('created : ' + createdBy);
	console.log('assignedBy : ' + assignedBy);
	console.log('assigned : ' + assigned);
	console.log('After Job Status : ' + status);
	console.log('Before Job Status : ' + BeforeStatus);



	if (!data.after.val()) 
	{
		console.log('A notification has been deleted .');
	}
	
	if (BeforeStatus != status && status != null && assignedBy != createdBy){
	const DeviceTokenAssigned = admin.database().ref(`/User/${assignedBy}/fcmtoken`).once('value');
	return DeviceTokenAssigned.then(result => 
	{
		const token_porter_id = result.val();
		console.log("AssignedBy Token ID: " + token_porter_id);
		console.log('assignedBy : ' + assignedBy);
		const payload = 
		{
			notification:
			{
				title: "Update to Job",
				body: 'Current status to Job: '  +  caseID + ' is ' +  status  + '.',
				icon: "default"

			}
		};

		
		return admin.messaging().sendToDevice(token_porter_id, payload)
		.then(response => 
			{
				console.log('This was a notification feature for assigned recep.');
			});
	});
	}
	if (BeforeStatus != status && status != null){
	const DeviceTokenAssigned = admin.database().ref(`/User/${createdBy}/fcmtoken`).once('value');
	return DeviceTokenAssigned.then(result => 
	{
		const token_porter_id = result.val();
		console.log("AssignedBy Token ID: " + token_porter_id);
		console.log('createdBy : ' + createdBy);
		const payload = 
		{
			notification:
			{
				title: "Update to Job",
				body: 'Current status to Job: '  +  caseID + ' is ' +  status  + '.',
				icon: "default"

			}
		};

		
		return admin.messaging().sendToDevice(token_porter_id, payload)
		.then(response => 
			{
				console.log('This was a notification feature for assigned recep.');
			});
	});
	}

});



