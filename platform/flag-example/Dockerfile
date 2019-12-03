FROM golang:1.13
WORKDIR /flag
COPY . /flag/
RUN GOOS=linux GOARCH=amd64 CGO_ENABLED=0 go build

FROM alpine:latest
RUN apk --no-cache add ca-certificates
WORKDIR /flag/
COPY --from=0 /flag/flag-example .
CMD ["./flag-example"]