.DEFAULT_GOAL := help


### QUICK
# ¯¯¯¯¯¯¯


ktail: server.kubetail  ## Tail logs multiple kubernetes pods (example: make ktail name=server)


include attachments/makefiles/help.mk
include attachments/makefiles/helm-charts.mk
