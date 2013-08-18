<?php
/**
 * File Name: contact_form_handler.php
 *
 * Send message function to process contact form submission
 *
 */

	$name    = '';
	$email   = '';
	$subject = '';
	$message = '';

    if(isset($_POST['email'])){

        $name = $_POST['name'];
        $email = $_POST['email'];
        $subject = $_POST['reason'];
        $message = $_POST['message'];


        if(get_magic_quotes_gpc()) {
            $message = stripslashes($message);
        }

		$address = "robot@psdtohtmlwp.com";

        $e_subject = "Knowledge Base: 'You Have Received a Message From"  . $name . '.';
	        
        $e_body = 	"You have Received a message from: "
            .$name
            . "\n"
            ."Their additional message is as follows."
            ."\r\n\n";

        $e_content = "\" $message \"\r\n\n";

        $e_reply = 	 "You can contact "
            .$name
            . " via email,"
            .$email;

        $msg = $e_body . $e_content . $e_reply;

        mail($address, $e_subject, $msg, "From: $email\r\nReply-To: $email\r\nReturn-Path: $email\r\n","-f $address");
        
			echo "Message Sent Successfully!";		
        }
		else{        
             echo "Message Sending Failed!";
        }
	
?>