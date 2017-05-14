//returns a string representation of the input
//note: sorry the output is not very pretty...
function printAST(a){
    //where the result will be stored    
    var result = "{ \n";
    
    function helper(result, a){
        //go through all the key possibilities
        for(var i = 0; i<12; i++){
            // var key store the key string
            var key = Object.keys(a)[i];
            //if it stores another object, look if it's null or not
            if(key == "templateinvocation" || key == "templatedef" || key == "itext" || key == "targs" || key == "tparam" || key == "next" || key == "dtext"){
               
                if(a[Object.keys(a)[i]] == null){
                   result = result + Object.keys(a)[i] + ": " + a[Object.keys(a)[i]] + "," +'\n';
               }
                
                else{
                    result = result + Object.keys(a)[i] + ": " + "\n";
                    result = result + printAST(a[Object.keys(a)[i]]) ;
                }
            }
            //print the key:value
            else if(key == "name" || key == "OUTERTEXT" || key == "INNERTEXT" || key == "INNERDTEXT" || key == "pname"){
                result = result + Object.keys(a)[i] + ": " + '"' + a[Object.keys(a)[i]] +'",'+ '\n';
            }
            
        }
        return result + "}," + '\n';
    }
    return helper(result, a);
}