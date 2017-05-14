
function parse(s){
    //parseTree is the object that contains AST
    //token set for the first token
    var parseTokenSet = {
        TSTART:true,
        DSTART:true,
        PSTART:true,
        OUTERTEXT:true,  
    };
    
    //Evaluate the first token to know which method to call first
    if(scan(s, parseTokenSet).token == "TSTART"){
       return parseTemplateInvocation(s);
       }
    else if(scan(s, parseTokenSet).token == "PSTART"){
        return parseTParam(s);
    }
    else if(scan(s, parseTokenSet).token == "DSTART"){
        return parseTemplateDef(s);
    }
    else if(scan(s, parseTokenSet).token == "OUTERTEXT"){
        return parseOuter(s);
    }
    
    
    //for <itext>
    function parseItext(s) {
    //tokenset for <itext>
        var itextTokenSet = {
        TSTART:true,
        DSTART:true,
        PSTART:true,
        PIPE:true,
        TEND:true,
        INNERTEXT:true,  
        };

        var parseTree = {}; // Object AST

        parseTree.name="itext";
        parseTree.templateinvocation= null;
        parseTree.templatedef = null;
        parseTree.tparam = null;
        parseTree.INNERTEXT = null;
        parseTree.next = null;

        //Compare the token with the different possibilities
        if(scan(s, itextTokenSet).token == "TSTART" || scan(s, itextTokenSet).token == "PIPE"){
            parseTree.templateinvocation = parseTemplateInvocation(s);
            s = parseTree.templateinvocation.a;
            parseTree.a = s;
        }

        else if(scan(s, itextTokenSet).token == "DSTART"){
            parseTree.templatedef = parseTemplateDef(s);
            s = parseTree.templatedef.a;
            parseTree.a = s;

        }
        else if(scan(s, itextTokenSet).token == "PSTART"){
            parseTree.tparam = parseTParam(s);
            s = parseTree.tparam.a;
            parseTree.a = s;
        }
        else if(scan(s, itextTokenSet).token == "INNERTEXT"){
            parseTree.INNERTEXT = scan(s, itextTokenSet).tokenvalue;
            s = s.substr(scan(s, itextTokenSet).tokenvalue.length);
            parseTree.a = s;
        }
        //if it's an innertext but without anything else at the end
        else if (scan(s, itextTokenSet).token == ""){
            parseTree.INNERTEXT = scan(s, itextTokenSet).tokenvalue;
        }

        //if s is not empty, look if there's a .next
        if(s != ""){
            if(scan(s, itextTokenSet).token != "TEND" && (scan(s, itextTokenSet).token == "TSTART" || scan(s, itextTokenSet).token == "DSTART" || scan(s, itextTokenSet).token == "PSTART" || scan(s, itextTokenSet).token == "INNERTEXT")){
                parseTree.next = parseItext(s);
            }
        }

        return parseTree;
    }

    //FOR <templateinvocation>
    function parseTemplateInvocation(s) {

        //tokenset for <templateinvocation>
        var TempInvocTokenSet = {
            TSTART: true,
            TEND: true,
            DSTART:true,
            PIPE: true,
            PSTART: true,
            INNERTEXT: true,
        };

        var parseTree = {}; // Object AST

        //if the current token is an TSTART
        if (scan(s, TempInvocTokenSet).token == 'TSTART') {
            s = s.substr(scan(s, TempInvocTokenSet).tokenvalue.length);
            parseTree.name = 'templateinvocation';
            if(scan(s, TempInvocTokenSet).token == "INNERTEXT" || scan(s, TempInvocTokenSet).token == "TSTART" || scan(s, TempInvocTokenSet).token == "DSTART" || scan(s, TempInvocTokenSet).token == "PSTART"){
                parseTree.itext = parseItext(s);
                s = parseTree.itext.a;
                //if the next token is | 
                if(scan(s, TempInvocTokenSet).token == "PIPE"){
                    parseTree.targs = parseTargs(s);
                    s = parseTree.targs.a;
                }
                else{
                    parseTree.targs = null;
                }
            }
            else{
                parseTree.itext = null;
                //if the next token is | 
                if(scan(s, TempInvocTokenSet).token == "PIPE"){
                    parseTree.targs = parseTargs(s);
                    s = parseTree.targs.a;
                }
                else{
                    parseTree.targs = null;
                }
            }

            if(scan(s, TempInvocTokenSet).token == 'TEND'){
                s = s.substr(scan(s, TempInvocTokenSet).tokenvalue.length);
                parseTree.a = s;
            }
            return parseTree;
        }    
    }

    // FOR <outer>
    function parseOuter(s) {

        //tokenset for <outer>
        var outerTokenSet = {
          OUTERTEXT: true,
          TSTART: true,
          DSTART: true,
        };

        var parseTree = {}; // Object AST
        var nextS;          // The next string that we will pass to parse method
        var nexttoken;      // The next token we have to evaluate

        //if the next string we want to pass to parse is not empty define nextS and nexttoken 
        if(s.substr(scan(s, outerTokenSet).tokenvalue.length) != "" && scan(s, outerTokenSet).token != ""){ 
            nextS = s.substr(scan(s, outerTokenSet).tokenvalue.length);
            nexttoken = scan(nextS, outerTokenSet).token;
        }
        //if it's empty, nexttoken and nextS will be empty string
        else{
            nextS = "";
            nexttoken="";
        }

        //if the current token is an OUTERTEXT
        if (scan(s, outerTokenSet).token == 'OUTERTEXT') {
            //pass value to the object parseTree
            parseTree.name = 'outer';
            parseTree.OUTERTEXT = scan(s, outerTokenSet).tokenvalue;
            parseTree.templateinvocation = null;
            parseTree.templatedef = null;

            //if the next token is a OUTERTEXT, a TSTART or a DSTART, then next will be another object
            if (nexttoken == 'OUTERTEXT' || nexttoken == 'TSTART' || nexttoken == 'DSTART') {
              s = s.substr(scan(s, outerTokenSet).tokenvalue.length);
              parseTree.next = parseOuter(s);
            } 
            else if(nexttoken == ''){
               parseTree.next = parseOuter(s);
            }
            else {
              parseTree.next = null;
            }
            parseTree.a = s;
            return parseTree;
        } 

        //if the current token is a TSTART
        else if ((scan(s, outerTokenSet).token) == 'TSTART') {
           //pass value to the object parseTree
            parseTree.name = 'outer';
            parseTree.OUTERTEXT = null;
            parseTree.templateinvocation = parseTemplateInvocation(s);
            s = parseTree.templateinvocation.a;
            parseTree.templatedef = null;

            if(s != ""){
               //if the next token is a OUTERTEXT, a TSTART or a DSTART, then next will be another object
                if (scan(s, outerTokenSet).token == 'OUTERTEXT' || scan(s, outerTokenSet).token == 'TSTART' || scan(s, outerTokenSet).token == 'DSTART') {
                  parseTree.next = parseOuter(s);
                }
                else if(scan(s, outerTokenSet).token == ""){
                    parseTree.next = parseOuter(s);
                }
            }
            else {
              parseTree.next = null;
            }
            parseTree.a = s;
            return parseTree;
        } 

        //if the current token is a DSTART
        else if (scan(s, outerTokenSet).token == 'DSTART') {
            parseTree.name = 'outer';
            parseTree.OUTERTEXT = null;
            parseTree.templateinvocation = null;
            parseTree.templatedef = parseTemplateDef(s);
            s = parseTree.templatedef.a;

            if(s != ""){
               //if the next token is a OUTERTEXT, a TSTART or a DSTART, then next will be another object
                if (scan(s, outerTokenSet).token == 'OUTERTEXT' || scan(s, outerTokenSet).token == 'TSTART' || scan(s, outerTokenSet).token == 'DSTART') {
                  parseTree.next = parseOuter(s);
                }
                else if(scan(s, outerTokenSet).token == ""){
                    parseTree.next = parseOuter(s);
                }
            }
            else {
              parseTree.next = null;
            }
            parseTree.a = s;
            return parseTree;
        }
        //if it's an OUTERTEXT but alone without something at the end
        else if (scan(s, outerTokenSet).token == ""){
            parseTree.name = 'outer';
            parseTree.OUTERTEXT = s;
            parseTree.templateinvocation = null;
            parseTree.templatedef = null;
            parseTree.next = null;
            return parseTree; 
        }
    }

    //for <tparam>
    function parseTParam(s) {

        //tokenset for <tparam>
        var TParamTokenSet = {
            PSTART: true,
            PEND: true,
            PNAME: true,
        };

        var parseTree = {}; // Object AST

        //if the current token is an PSTART, remove it from the string and parse again
        if (scan(s, TParamTokenSet).token == 'PSTART') {
            s = s.substr(scan(s, TParamTokenSet).tokenvalue.length);
            return parseTParam(s);
        }

        else if(scan(s, TParamTokenSet).token == "PNAME"){
            parseTree.name = 'tparam';
            parseTree.pname = scan(s, TParamTokenSet).tokenvalue;
        }
        else{
            parseTree.name = 'tparam';
            parseTree.pname = null;

        }
        s = s.substr(scan(s, TParamTokenSet).tokenvalue.length);
        if(scan(s, TParamTokenSet).token == "PEND"){
            s = s.substr(scan(s, TParamTokenSet).tokenvalue.length);
        }
        parseTree.a = s;
        return parseTree;    
    }

    //for <targs>
    function parseTargs(s) {

        //tokenset for <targs>
        var targsTokenSet = {
            PIPE: true,
            PEND: true,
            TSTART:true,
            DSTART:true,
            PSTART:true,
            INNERTEXT: true,  
        };

        var parseTree = {}; // Object AST

        //if the current token is an PIPE, remove it from the string
        if (scan(s, targsTokenSet).token == 'PIPE') {

            s = s.substr(scan(s, targsTokenSet).tokenvalue.length);
            parseTree.name = "targs";

            if(scan(s, targsTokenSet).token == 'TSTART' || scan(s, targsTokenSet).token == 'DSTART' || scan(s, targsTokenSet).token == 'PSTART' || scan(s, targsTokenSet).token == 'INNERTEXT' || scan(s, targsTokenSet).token == ''){
                parseTree.itext = parseItext(s);
                s = parseTree.itext.a;
            }
            if(s != ""){  
                if(scan(s, targsTokenSet).token == "PIPE"){
                   parseTree.next = parseTargs(s);
                }
            }

            else{
                parseTree.next = null;
            } 
        }
        parseTree.a = s;
        return parseTree;
    }

    //for <dtext>
    function parsedtext(s) {
    //tokenset for <tparam>
        var dtextTokenSet = {
        TSTART:true,
        DSTART:true,
        PSTART:true,
        PIPE:true,
        TEND:true,
        INNERDTEXT: true,  
        };

        var parseTree = {}; // Object AST

        parseTree.name="dtext";
        parseTree.templateinvocation= null;
        parseTree.templatedef = null;
        parseTree.tparam = null;
        parseTree.INNERDTEXT = null;
        parseTree.next = null;

        if(scan(s, dtextTokenSet).token == "TSTART" || scan(s, dtextTokenSet).token == "PIPE"){
            parseTree.templateinvocation = parseTemplateInvocation(s);
            s = parseTree.templateinvocation.a;
            parseTree.a = s;
        }

        else if(scan(s, dtextTokenSet).token == "DSTART"){
            parseTree.templatedef = parseTemplateDef(s);
            s = parseTree.templatedef.a;
            parseTree.a = s;

        }
        else if(scan(s, dtextTokenSet).token == "PSTART"){
            parseTree.tparam = parseTParam(s);
            s = parseTree.tparam.a;
            parseTree.a = s;
        }
        else if(scan(s, dtextTokenSet).token == "INNERDTEXT"){
            parseTree.INNERDTEXT = scan(s, dtextTokenSet).tokenvalue;
            s = s.substr(scan(s, dtextTokenSet).tokenvalue.length);
            parseTree.a = s;
        }
        else if (scan(s, dtextTokenSet).token == ""){
            parseTree.INNERTEXT = scan(s, dtextTokenSet).tokenvalue;
        }

        if(s != ""){
            if(scan(s, dtextTokenSet).token != "TEND" && (scan(s, dtextTokenSet).token == "TSTART" || scan(s, dtextTokenSet).token == "DSTART" || scan(s, dtextTokenSet).token == "PSTART" || scan(s, dtextTokenSet).token == "INNERDTEXT")){
                parseTree.next = parsedtext(s);
            }
        }
        return parseTree;
    }

    //Parse <Templatedef>
    function parseTemplateDef(s) {

        //tokenset for <templatedef>
        var TempDefTokenSet = {
            DSTART: true,
            TSTART: true,
            PIPE: true,
            PSTART: true,
            DEND: true,
            INNERDTEXT: true,
        };

        var parseTree = {}; // Object AST

        //if the current token is an DSTART
        if (scan(s, TempDefTokenSet).token == 'DSTART') {
            s = s.substr(scan(s, TempDefTokenSet).tokenvalue.length);
        }
        parseTree.name = 'templatedef';
        parseTree.dtext1 = null;
        if(scan(s, TempDefTokenSet).token != "DEND" && scan(s, TempDefTokenSet).token != "PIPE"){
            parseTree.dtext = parsedtext(s);
            s = parseTree.dtext.a;
        }
        if(scan(s, TempDefTokenSet).token == 'PIPE'){
                s = s.substr(scan(s, TempDefTokenSet).tokenvalue.length);
                parseTree.next = parseTemplateDef(s);    
        }

        else if(scan(s, TempDefTokenSet).token == 'DEND'){  
            s = s.substr(scan(s, TempDefTokenSet).tokenvalue.length);
            parseTree.next = null;
        }

        parseTree.a = s;
        return parseTree;   

    }
    
}