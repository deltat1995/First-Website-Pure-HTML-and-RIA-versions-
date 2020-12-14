(function(){
    // page components
    var dirHandler,docHandler,
    pageOrchestrator = new PageOrchestrator(); // main controller

    window.addEventListener("load", () => {
        pageOrchestrator.start(); // initialize the components
        pageOrchestrator.refresh(); // display initial content
    }, false);


    function PersonalMessage(username, messagecontainer) {
        this.username = username;
        
        this.show = function() {
          messagecontainer.textContent = this.username;
        };
    };
    
    function DirHandler(params) {
        this.dirContainer = params["dirList"];
        this.dirForm = params["dirForm"];
		this.selectedSubDirTitle = params["selectedSubDirTitle"];
        this.subDirForm = params["subDirForm"];
        this.rightTitle = params["rightTitle"];
        this.orchestrator = params["orchestrator"];
        
        
        this.registerEvents = function(params) {
            var addDirButton = params["addDirButton"];
			var trash = params["trash"];
			
            addDirButton.addEventListener("click", (e) => {
                e.preventDefault();
                this.orchestrator.resetRightColumn();
                this.showAddDir();
            })
            this.dirForm.querySelector("button").addEventListener("click", (e) => {
                e.preventDefault();
                let form = e.target.closest("form");
                this.createDir(form);
            });
            this.subDirForm.querySelector("button").addEventListener("click", (e) => {
                e.preventDefault();
                let form = e.target.closest("form");
                this.createSubDir(form);
            });
            
            trash.ondragover = (e) => {e.preventDefault();};
            trash.ondrop = (e) => {
                e.preventDefault();
                var type = e.dataTransfer.getData("type");
                switch(type){
                    case "dir": 
                        var dirId = parseInt(e.dataTransfer.getData("dirId"),10);
                        this.deleteDir(dirId);
                        break;
                    case "doc":
                        var docId = parseInt(e.dataTransfer.getData("docId"),10);
                        this.deleteDoc(docId);
                        break;
                    default: break;
                }
            }; 
        };
        
        this.fillDirsList = function(dirsArray) {
            
            dirsArray.forEach((folderList) => {
                var parent = folderList.parent;
                var childs = folderList.childs;
                var item = document.createElement("li");
                var span = document.createElement("span");
                span.innerHTML ="<i class='far fa-folder-open fa-2x'></i> "+parent.name;
                span.draggable=true;
                span.ondragstart = (e) => {
                    e.dataTransfer.setData("type","dir");
                    e.dataTransfer.setData("dirId",parent.ID);
                }
                item.appendChild(span);
                var popup = document.createElement("div");
                popup.className="infoPopup";
                popup.textContent="Creata il "+parent.creationDate;
                item.appendChild(popup);
                var anchor = document.createElement("a");
                anchor.href="#";
                anchor.id="addSubDir";
                anchor.innerHTML = "<i class='fas fa-folder-plus'></i> Aggiungi sottocartella";
                anchor.onclick = (e) => {
                    e.preventDefault();
                    this.orchestrator.resetRightColumn();
                    this.showAddSubDir(parent.ID);
                }
                item.appendChild(anchor);
                var childList = document.createElement("ul");
                childList.className="subdirs";
                for (let child of childs){
                    let childItem = document.createElement("li");
                    let childSpan = document.createElement("span");
                    childSpan.innerHTML ="<i class='fa fa-folder fa-lg'></i> "+child.name;
                    childSpan.style.cursor = "pointer";
                    childSpan.setAttribute("subdirid",child.ID);
                    childSpan.onclick = (e) => {
                        let oldSelection = this.dirContainer.querySelector(".selected");
                        if (oldSelection) oldSelection.removeAttribute("class");
                        childSpan.className= "selected";
                        this.selectedSubDirTitle.innerHTML = "<i class='far fa-folder-open fa-2x'></i> "+child.name;
                        this.orchestrator.resetRightColumn();
                        this.orchestrator.selectSubDir(child.ID);
                    }
                    childSpan.draggable=true;
                    childSpan.ondragstart = (e) => {
                        e.dataTransfer.setData("type","dir");
                        e.dataTransfer.setData("dirId",child.ID);
                    }
                    childSpan.ondragover = (e) => {e.preventDefault();};
                    childSpan.ondrop = (e) => {
                        e.preventDefault();
                        var type = e.dataTransfer.getData("type");
                        if (type === "doc"){
                            var docId = parseInt(e.dataTransfer.getData("docId"),10);
                            this.moveDoc(docId,child.ID);
                        }
                    };
                    childItem.appendChild(childSpan);
                    let childPopup = document.createElement("div");
                    childPopup.className="infoPopup";
                    childPopup.textContent="Creata il "+child.creationDate;
                    childItem.appendChild(childPopup);
                    childList.appendChild(childItem);
                }
                item.appendChild(childList);
                this.dirContainer.appendChild(item);
            });
        };
        
        this.getDirs = function(subDirId){
            //richiedi lista di cartelle e sottocartelle
            var self = this;
            sendData("GET","GetDirsList",null, function(req) {
                if (req.readyState == 4) {
                    var message = req.responseText;
                    if (req.status == 200) {
                        self.fillDirsList(JSON.parse(req.responseText));
                        if(subDirId) self.dirContainer.querySelector("[subdirid='"+subDirId+"']").dispatchEvent(new Event("click"));
                    } else {
                        self.orchestrator.showError(message);
                    }
                } 
            });
        };
        
        this.showAddDir = function(){
            this.rightTitle.textContent = "Scegli un nome per la nuova cartella:";
            this.rightTitle.hidden = false;
            this.dirForm.hidden = false;
        };
        
        this.moveDoc = function(docId,subDirId){
            //richiedi spostamento di un documento con id=docId nella sottocartella con id=subDirId
            if(subDirId === this.orchestrator.selectedSubDirId) return; //Non spostare il documento nella sua stessa sottocartella
            var self = this;
            var url = "MoveDoc?doc="+docId+"&dir="+subDirId;
            sendData("GET",url,null, function(req) {
                if (req.readyState == 4) {
                    var message = req.responseText;
                    if (req.status == 200) {
                        if (self.orchestrator.selectedDocId === docId)
                            self.orchestrator.selectedDocId = null;
                        self.orchestrator.refresh();
                    } else {
                        self.orchestrator.showError(message);
                    }
                } 
            });
        };
        
        this.createDir = function(form){
            //invia la richiesta con i dati per creare una cartella
            var self = this;
            sendData("POST","AddDir",new FormData(form), function(req) {
                if (req.readyState == 4) {
                    var message = req.responseText;
                    if (req.status == 200) {
                        form.reset();
                        self.orchestrator.refresh();
                    } else {
                        self.orchestrator.showError(message);
                    }
                } 
            });
        };
        
        this.showAddSubDir = function(dirId){
            this.rightTitle.textContent = "Scegli un nome per la nuova sottocartella:";
            this.rightTitle.hidden = false;
            this.subDirForm.querySelector("input[type='hidden']").value = dirId;
            this.subDirForm.hidden = false;
        };
        
        this.createSubDir = function(form){
            //invia la richiesta con i dati per creare una sottocartella
            var self = this;
            sendData("POST","AddSubDir",new FormData(form), function(req) {
                if (req.readyState == 4) {
                    var message = req.responseText;
                    if (req.status == 200) {
                        form.reset();
                        self.orchestrator.refresh();
                    } else {
                        self.orchestrator.showError(message);
                    }
                } 
            });
        };
        
        this.deleteDir = function(dirId) {
            //richiedi cancellazione di una cartelle/sottocartella
            if (confirm("Sei sicuro di voler continuare?")){
                var self = this;
                let url = "DelDir?dir="+dirId;
                sendData("GET",url,null, function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
	
                            self.orchestrator.selectedSubDirId=null;
                            self.orchestrator.selectedDocId=null;

                            self.orchestrator.refresh();
                        } else {
                            self.orchestrator.showError(message);
                        }
                    } 
                });
            }
        };
        
        this.deleteDoc = function(docId){
            //richiedi cancellazione di un documento
            if (confirm("Sei sicuro di voler continuare?")){
                var self = this;
                let url = "DelDoc?doc="+docId;
                sendData("GET",url,null, function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            if(self.orchestrator.selectedDocId === docId) {
                                self.orchestrator.selectedDocId=null;
                            }
                            self.orchestrator.refresh();
                        } else {
                            self.orchestrator.showError(message);
                        }
                    } 
                });
            }
        };
        
        this.hideForms= function(){
            this.rightTitle.hidden = true;
            this.dirForm.hidden = true;
            this.subDirForm.hidden = true;
        };
        
        this.reset = function(){
            this.selectedSubDirTitle.innerHTML = "";
            this.dirContainer.innerHTML = "";
        };
    };
    
    function DocHandler(params) {
        this.docContainer = params["docList"];
        this.docInfoContainer = params["docInfo"];
        this.docForm = params["docForm"];
        this.rightTitle = params["rightTitle"];
        this.orchestrator = params["orchestrator"];
        
        this.registerEvents = function(params) {
            var addDocButton = params["addDocButton"];
            addDocButton.addEventListener("click", (e) => {
                e.preventDefault();
                let subDirId = this.orchestrator.selectedSubDirId;
                if (!subDirId) {
                    this.orchestrator.showError("Devi prima selezionare la sottocartella in cui aggiungere il documento");
                    return;
                }
                this.orchestrator.resetRightColumn();
                this.showAddDoc(subDirId);
            });
            
            this.docForm.querySelector("button").addEventListener("click", (e) => {
                e.preventDefault();
                let form = e.target.closest("form");
                this.createDoc(form);
            });
            
        };
        
        this.fillDocsList = function (docsArray) {
            
            docsArray.forEach((doc)=>{
                var item = document.createElement("li");
                var span = document.createElement("span");
                span.innerHTML = "<i class='far fa-file-alt fa-lg'></i> "+doc.name;
                span.style.cursor = "pointer";
                span.setAttribute("docid",doc.ID);
                span.onclick = (e) => {
                    this.orchestrator.resetRightColumn();
                    let oldSelection = this.docContainer.querySelector(".selected");
                    if (oldSelection) oldSelection.removeAttribute("class");
                    span.className= "selected";
                    this.orchestrator.selectDoc(doc.ID);
                };
                span.draggable = true;
                span.ondragstart = (e) => {
                    e.dataTransfer.setData("type","doc");
                    e.dataTransfer.setData("docId",doc.ID);
                }
                item.appendChild(span);
                var popup = document.createElement("div");
                popup.className="infoPopup";
                popup.textContent="Creata il "+doc.creationDate;
                item.appendChild(popup);
                this.docContainer.appendChild(item);
            });
        };
                
        this.getDocuments = function(subDirId,docId){
            //fai richiesta dei documenti
            var self = this;
            var url = "GetDocsList?dir="+subDirId;
            sendData("GET",url,null, function(req) {
                if (req.readyState == 4) {
                    var message = req.responseText;
                    if (req.status == 200) {
                        self.fillDocsList(JSON.parse(req.responseText));
                        if(docId) self.docContainer.querySelector("[docid='"+docId+"']").dispatchEvent(new Event("click"));
                    } else {
                        self.orchestrator.showError(message);
                    }
                } 
            });
        };
        
        this.showDocInfo = function(doc){
            this.rightTitle.textContent = "Informazioni sul documento selezionato:";
            this.rightTitle.hidden = false;
            let fields = this.docInfoContainer.querySelectorAll("span");
            fields[0].textContent = doc.name;
            fields[1].textContent = doc.creationDate;
            fields[2].textContent = doc.type;
            this.docInfoContainer.querySelector("#sommario").textContent = doc.summary;
            this.docInfoContainer.hidden = false;
        };
        
        this.getDocInfo = function(docId){
            //richiedi info sul documento 
            var self = this;
            var url = "GetDocInfo?doc="+docId;
            sendData("GET",url,null, function(req) {
                if (req.readyState == 4) {
                    var message = req.responseText;
                    if (req.status == 200) {
                        self.showDocInfo(JSON.parse(req.responseText));
                    } else {
                        self.orchestrator.showError(message);
                    }
                } 
            });
        };
        
        this.showAddDoc = function(subDirId){
            this.rightTitle.textContent = "Fornisci i dati del nuovo documento:";
            this.rightTitle.hidden = false;
            this.docForm.querySelector("input[type='hidden']").value = subDirId;
            this.docForm.hidden = false;
        };
        
        this.createDoc = function(form){
            //invia i dati per creare un documento
            var self = this;
            sendData("POST","AddDoc",new FormData(form), function(req) {
                if (req.readyState == 4) {
                    var message = req.responseText;
                    if (req.status == 200) {
                        form.reset();
                        self.orchestrator.refresh();
                    } else {
                        self.orchestrator.showError(message);
                    }
                } 
            });
        };
        
        this.hideRightColumn=function() {
            this.rightTitle.hidden = true;
            this.docInfoContainer.hidden = true;
            this.docForm.hidden = true;
        };
        
        this.reset = function(){
            this.docContainer.innerHTML = "";
        };
    };
    
    function PageOrchestrator() {
        this.selectedSubDirId = null;
        this.selectedDocId = null;
        this.alert = document.getElementById("error");
        this.timer = null;
        
        this.start = function(){
            var pmsg = new PersonalMessage(sessionStorage.getItem('username'),document.getElementById("user"));
            pmsg.show();
            
            dirHandler = new DirHandler({
                dirList: document.getElementById("dirList"),
                dirForm: document.getElementById("addDirForm"),
				selectedSubDirTitle: document.querySelector("#selectedSubDir"),
                subDirForm: document.getElementById("addSubDirForm"),
                rightTitle: document.querySelector(".column.right .titleColumn span"),
                orchestrator: this
            });
            dirHandler.registerEvents({
				addDirButton: document.getElementById("addDir"),
				trash: document.getElementById("trash")
				});
            
            docHandler = new DocHandler({
                docList: document.getElementById("docList"),
                docInfo: document.getElementById("docInfo"),
                docForm: document.getElementById("addDocForm"),
                rightTitle: document.querySelector(".column.right .titleColumn span"),
                orchestrator: this
            });
            docHandler.registerEvents({
				addDocButton: document.getElementById("addDoc")
			});
            
            
        };
        
        this.selectSubDir = function(subDirId) {
        	docHandler.reset();
            if (this.selectedSubDirId !== subDirId) {
                this.selectedSubDirId = subDirId;
                this.selectedDocId = null;
                docHandler.getDocuments(subDirId,null);
            }else {
            	docHandler.getDocuments(this.selectedSubDirId,this.selectedDocId);
            }
        };
        
        this.selectDoc = function(docId){
            this.selectedDocId = docId;
            docHandler.getDocInfo(docId);
        };
        
        this.showError = function(error) {
            clearTimeout(this.timer);
            this.alert.textContent = error;
            this.alert.hidden = false;
            this.timer = setTimeout(()=>{
                this.alert.style.opacity=0;
                this.alert.hidden = true;
                this.alert.style.opacity=1;
                this.alert.textContent = "";},6000);
        };
        
        this.refresh = function(){
            this.resetRightColumn();
            dirHandler.reset();
            docHandler.reset();
            dirHandler.getDirs(this.selectedSubDirId);
        };
        
        this.resetRightColumn = function(){
            dirHandler.hideForms();
            docHandler.hideRightColumn();
        };
    };
})();