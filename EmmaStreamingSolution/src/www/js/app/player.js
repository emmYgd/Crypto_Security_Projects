define("myPlayer", "[myTeam]",
    function(require){
        //var myteam = require("./team");
        return{
            myFunc: function(){
                document.write("Name: " + myteam.player + ", Country: " + myteam.team);
            }
        }
});