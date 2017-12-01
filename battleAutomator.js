
	// create the page
	var webPage = require('webpage');
	var page = webPage.create();
	
		phantom.addCookie({
  'name'     : 'showdown_username',   /* required property */
  'value'    : 'FactsB',  /* required property */
  'domain'   : 'play.pokemonshowdown.com	',
  'path'     : '/',                /* required property */
  'httponly' : false,
  'secure'   : false,
  'expires'  : (new Date()).getTime() + (1000 * 60 * 60)	   /* <-- expires in 1 hour */
});
	
	phantom.addCookie({
  'name'     : 'sid',   /* required property */
  'value'    : '4377002xHqPEQ%2FX1EoaU3l5WBvvX	',  /* required property */
  'domain'   : '.pokemonshowdown.com',
  'path'     : '/',                /* required property */
  'httponly' : true,
  'secure'   : false,
  'expires'  :  (new Date()).getTime() + (1000 * 60 * 60)	  /* <-- expires in 1 hour */
});
	
	// Open the page
	page.open('http://play.pokemonshowdown.com', function() {

		setUp();
		battle();
		waitForever();
		// When things happen
	})	//page.open paren
	
	
function setUp(){
	setTimeout(function() {
		
		// Working qith jquery		
		page.includeJs("http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js", 
		function() {
			
			// J query clicking
			page.evaluate(function() {
				
				// Selecting gen1OU
				$('button[name="format"]').click();
				$('button[value="gen1ou"]').click();
				
				// selecting team
				$('button[name="team"]').click();
 				$('button[name="moreTeams"]').click();
 				$('button[name="selectTeam"][value="0"]').click();

			});// evaluate paren	
		});// include js paren
	}, 1000);	//setTimeout paren
}// setUp paren;

///////////////////						Above: setUp()	             ////////////////////
//////////////////						Below: Battle()							 ////////////////////


function battle() {
	setTimeout( function() {
		page.includeJs("http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js", 
			function() {
			
				// click battle
			page.evaluate(function() {
				$('button[name="search"]').click();
			});// evaluate paren	
		});// include js paren
		
		console.log("waiting for a battle");

		// Wait for the battle page to pop up - the url changing
		page.onUrlChanged = function() {
			// wait 10 seconds, for battle info to load
			setTimeout(function() {
				console.log("battle opened. url = " + page.url);
				oneTurn();
				
///////////////////////////////////////////////////////////////////////////////
///////////////////						THE LISTENER								   ///////////////////
				
				page.onConsoleMessage = function(msg) {
					console.log("SOMETHING HAPPENED: " + msg);

					// Check if turn has ended
					var jmsg = JSON.stringify(msg);
					
					var action = page.evaluate(function(jmsg) {
						
						
						var message = jQuery.parseJSON(jmsg);


						if(message.indexOf('|win|') !== -1)
							return 'gameOver';
						else if(message.indexOf('|faint|') !== -1){
							//console.log(message);
							return 'faint';
						}
						else if(message.indexOf('forefited') !== -1)
							return 'forefit';
						else if(message.indexOf('|turn|') !== -1 || message.indexOf('|upkeep') !== -1){
							if(message.indexOf('|switch|') !== -1 && message.indexOf('|turn|1') == -1){
								return 'turnSwitch';
							}
							console.log("\nUPKEEP, bihhhh\n");
							return 'newTurn'
						}
	
						else return 'noAction'
							
					}, jmsg);// page.evaluate		
 					//console.log("ACTION: " + action);
					
					if(action == 'newTurn'){
						console.log("\nNew Turn\n");
						oneTurn();
					}
					
					// Faint condition
					else if(action == 'faint'){
						//console.log("\nFaint\n");
						//console.log(msg);
				
							// Write to a file
 						var fs = require('fs');
						var path = 'state.txt';
 						fs.touch(path);	
						// just p1 faints
						if(msg.indexOf('|faint|p1a') !== -1 && msg.indexOf('|faint|p2a') == -1){
							//console.log("\nplayer1 fainted\n");
							fs.write(path, "1\n|faint|p1a\n", 'rw');
						}
						// just p2 faints
						else if(msg.indexOf('|faint|p2a') !== -1 && msg.indexOf('|faint|p1a') == -1){
							fs.write(path, "1\n|faint|p2a\n", 'rw');
							//console.log("\nPlayer 2 Fainted\n");
						}
						else {
							fs.write(path, "1\n|faint|p1a\n|faint|p2a\n", 'rw');
							//console.log("\nBoth players Fainted\n");
						}

						// Treat it as if it is a regular turn
						oneTurn();
					}//faint paren
					
					else if(action == 'turnSwitch'){
						// write to file
							var fs = require('fs');
							var path = 'state.txt';
							fs.touch(path);	
							fs.write(path, "4\n", 'rw');
							// p1 only
							if(msg.indexOf('|switch|p1a') !== -1 && msg.indexOf('|switch|p2a') == -1){
								console.log("p1 switch");
								fs.write(path, "1\n", 'a');
							}
							// p2 only
							else if(msg.indexOf('|switch|p1a') == -1 && msg.indexOf('|switch|p2a') !== -1){
								console.log("p2 switch");
								fs.write(path, "2\n", 'a');
							}
							else {
								console.log("both switch");
								fs.write(path, "21\n", 'a');
							}	
							oneTurn();
					}// trun switch
					else if(action == 'forefit'){
						phantom.exit();
					}
					else if(action == 'gameOver'){
						getInfoSendInfo();
						phantom.exit();
					}
					
			
				};// onConsoleMessage paren
				
			}, 5000);	// Wait 15 seconds for battle info to have loaded after the url change
		};// on url change paren
		
		}, 5000);//setTimeout paren, wait 5 seconds for page to have loaded
	}// battle paren

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
	
function oneTurn() {setTimeout(function(){
	
			console.log("in oneTurn()");
			// Extract
			
				getInfoSendInfo();
				
				var fs = require('fs');
 				var path = 'log.txt';
 				//create the file
 				fs.touch(path);	
 				fs.write(path, "State sent, waiting 3 seconds for action" + "\n", 'rw');
				
				getMoveMake();
			
			
			function test() {
				console.log("\nin this bihhh\n");
			}
	}, 2000);// set Timeout paren
	}//oneTurn paren	
	
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////	
	
	function getMoveMake() {
					setTimeout(function() {
		
					var move = "";
						
					// get move from AI
					// TO DO: add check for existence of file				
					var sf = require('fs');
					move = sf.read('action.txt');
					
					console.log("move: " + move);
					
					var fs = require('fs');
					var path = 'log.txt';
					fs.write(path, "in getMoveMake(), recieved move " + move, 'a');
					
						// MAKE THE MOVE:
					// stringify the move so that it can be passed into evaluate
					var json = JSON.stringify(move);
					page.evaluate(function(json) {
						
						// json string back to string
						var move = jQuery.parseJSON(json);
						// Switch Statement based on move
						switch (move) {
							case '0':
									$('button[name="chooseMove"][value="1"]').click();
									break;
							case '1':
									$('button[name="chooseMove"][value="2"]').click();
									break;
							case '2':
									$('button[name="chooseMove"][value="3"]').click();
									break;
							case '3':
									$('button[name="chooseMove"][value="4"]').click();
									break;
							case 'Exeggutor':
									$('button[name="chooseSwitch"]:contains("Exeggutor")').click();
									break;
							case 'Tauros':
									$('button[name="chooseSwitch"]:contains("Tauros")').click();
									break;
							case 'Jolteon':
									$('button[name="chooseSwitch"]:contains("Jolteon")').click();
									break;
							case 'Slowbro':
									$('button[name="chooseSwitch"]:contains("Slowbro")').click();
									break;
							case 'Golem':
									$('button[name="chooseSwitch"]:contains("Golem")').click();
									break;
							case 'Chansey':
									$('button[name="chooseSwitch"]:contains("Chansey")').click();
							default: 
								$('button[name="chooseMove"][value="1"]').click();
						}///switch paren
					}, json);		// evaluate(move) paren
					
				}, 3000);
			}// getMoveMake
	
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////	
	
	function getInfoSendInfo() {
		
		page.includeJs("http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js", 
				function() {
							
				// get pokeInfo
				var p1info = page.evaluate(function() {
					return $('.statbar:nth-child(1)').text();
				});
				console.log(p1info);
							
				//Get opposing pokeInfo
				var p2info = page.evaluate(function() {
					return $('.statbar:nth-child(2)').text();
				});
				console.log(p2info);
							
				// Write to a file
 				var fs = require('fs');
 				var path = 'state.txt';
 				//create the file
 				fs.touch(path);	
 				fs.write(path, p1info + "\n" + p2info, 'rw');
				
				// If it is the first turn, we output which player we are
				var player = page.evaluate(function() {
					var turn =  $('div[class="turn"]').text();
					if(turn === 'Turn 1'){
						var player1 = $('div[class="chat"]').text();
						
						
						if (player1.substring(0, 6) == 'FactsB')
							return ("p1a\n");
						else return("p2a\n");
					}
					else return "";
				});// evaluate paren	
				console.log(player);
				fs.write(path, player + "3\n" + p1info + "\n" + p2info);
				
			});// include js paren
	}
				
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
				
				
				// Wait 25 minutes then die
		function waitForever() {
			setTimeout(function() {
				console.log("in wait2");
				phantom.exit();
			}, 1500000);
		}// waitForever paren
		
