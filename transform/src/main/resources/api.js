/* 
 * render function called from LessEngine 
 */
function lessToCss(input) {

	/* setup the parser*/
	var parser = new less.Parser({
		optimization : 2
	});

	/* invoke the parser */
	var result;
	parser.parse(input, function(err, tree) {
		
		/* fail for any errors */
		if (err) {
			throw err;
		}
		
		/* CSS transformation */
		result = tree.toCSS();
		
	});
	return result;
	
};
