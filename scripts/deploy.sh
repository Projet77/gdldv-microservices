#!/bin/bash

# ============================================
# GDLDV Microservices Deployment Script
# ============================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Functions
print_header() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ $1${NC}"
}

# Check prerequisites
check_prerequisites() {
    print_header "Checking Prerequisites"

    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    print_success "Docker is installed"

    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    print_success "Docker Compose is installed"

    if ! docker info &> /dev/null; then
        print_error "Docker daemon is not running. Please start Docker."
        exit 1
    fi
    print_success "Docker daemon is running"
}

# Create .env file if not exists
setup_env() {
    print_header "Setting Up Environment"

    if [ ! -f .env ]; then
        print_warning ".env file not found"
        print_info "Copying .env.example to .env"
        cp .env.example .env
        print_warning "Please update .env file with your configuration before deploying!"
        read -p "Press Enter to continue or Ctrl+C to abort..."
    else
        print_success ".env file found"
    fi
}

# Build services
build_services() {
    print_header "Building Services"

    print_info "Building Docker images (this may take several minutes)..."
    docker-compose -f docker-compose-full.yml build --no-cache

    print_success "All services built successfully"
}

# Start services
start_services() {
    print_header "Starting Services"

    print_info "Starting infrastructure services first..."
    docker-compose -f docker-compose-full.yml up -d mysql-vehicle mysql-reservation mysql-user mysql-rental
    sleep 10

    docker-compose -f docker-compose-full.yml up -d eureka-server
    print_info "Waiting for Eureka Server to be healthy..."
    sleep 30

    docker-compose -f docker-compose-full.yml up -d config-server
    print_info "Waiting for Config Server to be healthy..."
    sleep 20

    docker-compose -f docker-compose-full.yml up -d api-gateway
    print_info "Waiting for API Gateway to be healthy..."
    sleep 20

    print_info "Starting business services..."
    docker-compose -f docker-compose-full.yml up -d vehicle-service reservation-service user-service
    sleep 30

    docker-compose -f docker-compose-full.yml up -d rental-service
    sleep 20

    print_success "All services started successfully"
}

# Check services health
check_health() {
    print_header "Checking Services Health"

    docker-compose -f docker-compose-full.yml ps

    echo ""
    print_info "Service URLs:"
    echo "  - Eureka Dashboard: http://localhost:8761"
    echo "  - API Gateway: http://localhost:8000"
    echo "  - Vehicle Service: http://localhost:8001/swagger-ui.html"
    echo "  - Reservation Service: http://localhost:8002/swagger-ui.html"
    echo "  - User Service: http://localhost:8003/swagger-ui.html"
    echo "  - Rental Service: http://localhost:8004/swagger-ui.html"
}

# Main deployment
main() {
    print_header "GDLDV Microservices Deployment"

    check_prerequisites
    setup_env
    build_services
    start_services
    check_health

    print_success "Deployment completed successfully!"
}

# Run main function
main
