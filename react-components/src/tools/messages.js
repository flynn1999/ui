export default {
    locales: "en-US",
    messages: {
        editTool: "Edit {name}",
        addTool: "Add Tool",
        toolName: "Tool Name",
        toolDesc: "Description",
        toolVersion: "Version",
        attribution: "Attribution",
        location: "Location",
        imageName: "Image Name",
        toolImplementation: "Tool Implementation",
        dockerHubURL: "Docker Hub URL",
        type: "Type",
        osgImagePath: "OSG Image Path",
        entrypoint: "Entrypoint",
        workingDirectory: "Working Directory",
        containerUID: "UID",
        maxCPUCores: "Max CPU Cores",
        memoryLimit: "Memory Limit",
        minDiskSpace: "Min Disk Space",
        save: "Save",
        cancel: "Cancel",
        port: "Port",
        containerPorts: "Container Ports",
        portNumber: "Port Number",
        noContainerPorts: "No Container Ports",
        restrictions: "Restrictions",
        restricted: "Restricted",
        restrictionsSupport:
            "If your tool requires updated restrictions, please contact CyVerse support.",
        pidsLimit: "PIDs Limit",
        networkMode: "Network Mode",
        timeLimit: "Time Limit (seconds)",
        implementor: "Implementor",
        implementorEmail: "Implementor Email",
        add: "Add",
        noFiles: "No sample files",
        fileName: "File Name",
        sampleInputFiles: "Sample Input Files",
        sampleOutputFiles: "Sample Output Files",
        containerImage: "Container Image",
        tag: "Tag",
        interactive: "Interactive",
        containerName: "Container Name",
        entrypointWarning:
            "WARNING: Do not add a tool without an Entry Point setting if its Docker " +
            "image also does not have a default `ENTRYPOINT`. If a tool like this is required, then its " +
            "Network Mode setting should be set to `none` to contain any risky scripts run by this tool.",
        minMemoryLimit: "Min Memory Limit",
        cpuShares: "CPU Shares",
        minCPUCores: "Min CPU Cores",
        skipTmpMount: "Skip Tmp Mount",
        containerDevices: "Container Devices",
        device: "Device",
        noContainerDevices: "No Container Devices",
        hostPath: "Host Path",
        containerPath: "Container Path",
        containerVolumes: "Container Volumes",
        volume: "Volume",
        noContainerVolumes: "No Container Volumes",
        containerVolumesFrom: "Container Volumes From",
        volumeFrom: "Volume From",
        noContainerVolumesFrom: "No Container Volumes From",
        name: "Name",
        namePrefix: "Name Prefix",
        url: "URL",
        readOnly: "Read Only",
        bindToHost: "Bind to Host",
        volumesWarning:
            "WARNING: Do not add Container Volumes or Container Volumes From settings to " +
            "tools unless it is certain that tool is authorized to access that data.",
        makePublic: "Make Public",
        public: "Public",
        noTools: "No tools",
        allTools: "All",
        myTools: "Only my tools",
        publicTools: "Public tools",
        requestToolMI: "Request Tool",
        edit: "Edit",
        delete: "Delete",
        useToolInApp: "Use in app",
        shareWithCollaborators: "Share with collaborators",
        tools: "Tools",
        share: "Share",
        searchTools: "Search tools",
        refresh: "Refresh",
        imageNameHelp:
            "If the image is in Docker Hub, this field should be in username/image-name" +
            " format, where username is your Docker Hub username. If it's in another registry, such as the CyVerse registry, it should be in registry-host/image-name format.",
        validationErrMinCPUsGreaterThanMax: "Must be less than Max CPU Cores",
        validationErrMaxCPUsLessThanMin: "Must be greater than Min CPU Cores",
        validationErrMinRAMGreaterThanMax: "Must be less than Max Memory",
        validationErrMaxRAMLessThanMin: "Must be greater than Min Memory",
        validationErrMustBePositive: "Must be at least 0",
        newToolRequestDialogHeading: "New Tool Request",
        submit: "Submit",
        toolNameLabel: "What is the name of the tool?",
        toolDescLabel: "Briefly describe the tool:",
        toolSrcLinkLabel:
            "Provide a link for your tool's source (GitHub, BitBucket, DockerHub etc...):",
        toolVersionLabel: "What is your tool's version?",
        toolDocumentationLabel: "Provide a link for your tool's documentation:",
        toolInstructionsLabel:
            "Enter any other instructions or relevant information:",
        toolTestDataLabel: "Provide a link for your tool's test data:",
        validationErrMsgURL:
            "A valid URL must begin with either ftp or http or https.",
        toolAttributionLabel: "Attribution",
        descriptionLabel: "Description",
        restrictionsLabel: "Restrictions",
        memoryLimitLabel: "Memory Limit (bytes)",
        pidsLimitLabel: "PIDs Limit",
        secondsLimitLabel: "Maximum Run Time (seconds)",
        networkingLabel: "Networking",
        toolInformationLbl: "Tool Information",
        appsUsingToolLbl: "Apps using this tool",
    },
};
