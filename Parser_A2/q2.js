var tokenset = {
    TSTART: true,
    TEND: true,
    PIPE: true,
    OUTERTEXT: true,
    INNERTEXT: true,
    DSTART: true,
    DEND: true,
    INNERDTEXT: true,
    PSTART: true,
    PEND: true,
    PNAME: true,
};

//scan the first token of the string and return the token value and the token name 
function scan(s, tokenset){
    
    var tokenObj = {};     //the object describing the token at the start of the string
    var i;
    //go through all the keys from the tokenlist to find a match
    for(i=0; i<Object.keys(tokenset).length; i++){
       
        if( tokenset[Object.keys(tokenset)[i]] && (s.match(eval(Object.keys(tokenset)[i])) != null )){
            
            //to make sure it's really {{{ or {{
            if(Object.keys(tokenset)[i] == "TSTART"){
                if(tokenset["PSTART"] != undefined){
                    if(s.match(eval("PSTART")) != null){
                        tokenObj.token = "PSTART";
                        tokenObj.tokenvalue = "{{{";
                        return tokenObj;
                    }
                }
            }
           
            //to make sure it's really }} or }}}
            if(Object.keys(tokenset)[i] == "TEND"){
                if(tokenset["PEND"] != undefined){
                    if(s.match(eval("PEND")) != null){
                        tokenObj.token = "PEND";
                        tokenObj.tokenvalue = "}}}";
                        return tokenObj;
                    }
                }
            }
            //set object token name
            tokenObj.token = Object.keys(tokenset)[i];
            //set object token value 
            tokenObj.tokenvalue = (s.match(eval(Object.keys(tokenset)[i])))[0];
            //return object
            return tokenObj;
        }
    }
    if(i == Object.keys(tokenset).length){
        //set object token name
        tokenObj.token = "";
        //set object token value 
        tokenObj.tokenvalue = s;
        //return object
        return tokenObj;
    }
}

